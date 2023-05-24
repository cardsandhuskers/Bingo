package io.github.cardsandhuskers.bingo.handlers;

import io.github.cardsandhuskers.bingo.Bingo;
import io.github.cardsandhuskers.bingo.listeners.*;
import io.github.cardsandhuskers.bingo.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import io.github.cardsandhuskers.teams.objects.TempPointsHolder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static io.github.cardsandhuskers.bingo.Bingo.*;
import static io.github.cardsandhuskers.teams.Teams.handler;
import static org.bukkit.Bukkit.*;

public class GameStageHandler {
    private Bingo plugin;
    private WorldBorder worldBorder;

    public GameStageHandler(Bingo plugin) {
        this.plugin = plugin;
    }

    public void startGame() {
        BingoCardHandler bingoCardHandler = new BingoCardHandler(plugin);
        bingoCardHandler.initializeMaps();
        worldBorder = getWorld(plugin.getConfig().getString("world")).getWorldBorder();


        //listeners here, itemClick and however I check for pickup/craft need to be passed the bingoCardHandler
        getServer().getPluginManager().registerEvents(new InventoryClickListener(bingoCardHandler), plugin);
        getServer().getPluginManager().registerEvents(new ItemClickListener(bingoCardHandler), plugin);
        getServer().getPluginManager().registerEvents(new ItemThrowListener(), plugin);
        getServer().getPluginManager().registerEvents(new ItemCraftListener(bingoCardHandler), plugin);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(bingoCardHandler), plugin);
        getServer().getPluginManager().registerEvents(new ItemPickupListener(bingoCardHandler), plugin);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(plugin, this), plugin);


        pregameTimer();
    }
    public Location getWorldSpawn() {
        World world = getServer().getWorld(plugin.getConfig().getString("world"));
        Location spawnLoc = world.getSpawnLocation();
        Location testLoc = new Location(world, spawnLoc.getX(), 200, spawnLoc.getZ());
        while(testLoc.getBlock().getType() == Material.AIR) {
            testLoc.setY(testLoc.getY() - 1);
        }
        testLoc.setY(testLoc.getY() + 1);
        return testLoc;
    }

    public void pregameTimer() {
        Countdown pregameTimer = new Countdown((JavaPlugin)plugin,
                //should be 60
                plugin.getConfig().getInt("PregameTime"),
                //Timer Start
                () -> {
                    //Reset temp points for all teams
                    for(Team t:handler.getTeams()) {
                        t.resetTempPoints();
                    }
                    Bingo.gameState = Bingo.State.GAME_STARTING;

                    Location teleportLoc = getWorldSpawn();
                    //teleportLoc.getWorld().setSpawnLocation(teleportLoc.getBlockX(), teleportLoc.getBlockY(), teleportLoc.getBlockZ());

                    int counter = 0;
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                            p.teleport(teleportLoc);
                            p.setHealth(20);
                            p.setFoodLevel(20);
                            p.setSaturation(20);

                            Inventory inv = p.getInventory();
                            inv.clear();

                            ItemStack bingoCard = new ItemStack(Material.NETHER_STAR);
                            ItemMeta bingoCardMeta = bingoCard.getItemMeta();
                            bingoCardMeta.setDisplayName("Bingo Card");
                            bingoCard.setItemMeta(bingoCardMeta);
                            inv.setItem(8, bingoCard);

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                if(handler.getPlayerTeam(p) != null) p.setGameMode(GameMode.ADVENTURE);
                                else p.setGameMode(GameMode.SPECTATOR);
                            }, 5L);

                            p.setBedSpawnLocation(teleportLoc, true);
                        }, 20L * (counter/5));
                        counter++;

                    }

                    teleportLoc.getWorld().setTime(1000);
                    teleportLoc.getWorld().setClearWeatherDuration(24000);


                    World world = getServer().getWorld(plugin.getConfig().getString("world"));
                    Location spawn = world.getSpawnLocation();
                    worldBorder.setCenter(spawn.getX(),spawn.getZ());
                    worldBorder.setSize(25);

                },

                //Timer End
                () -> {
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2F);
                        p.sendTitle(ChatColor.GREEN + ">GO!<", "", 2, 16, 2);

                        if(handler.getPlayerTeam(p) != null) {
                            prepPlayer(p);
                        }
                    }
                    gameTimer();
                    worldBorder.setSize(plugin.getConfig().getInt("radius") * 2);
                },

                //Each Second
                (t) -> {
                    if(t.getSecondsLeft() == t.getTotalSeconds() - 2) {
                        Bukkit.broadcastMessage(ChatColor.STRIKETHROUGH + "----------------------------------------");
                        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Bingo!");
                        Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "How To Play:");
                        Bukkit.broadcastMessage("Everyone has the same bingo card with 25 items on it. " +
                                "\nYou will have " + ChatColor.YELLOW + "" + ChatColor.BOLD + plugin.getConfig().getInt("GameTime")/60 + ChatColor.RESET + " minutes to collect as many items as you can!" +
                                ChatColor.RED + "" + ChatColor.BOLD + "\nYou only need 1 of each item type to get points!" + ChatColor.RESET + " The number represents the number of teams that can still earn the item!" +
                                "\nStandard survival rules apply, mine, craft, and slay mobs to collect the items." +
                                "\nKeep Inventory is on and PvP is disabled for this game." +
                                "\nOnly the first six teams to obtain each item will be able to receive points! Speed matters!");
                        Bukkit.broadcastMessage(ChatColor.STRIKETHROUGH + "----------------------------------------");

                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        }
                    }

                    if(t.getSecondsLeft() == t.getTotalSeconds() - 12) {
                        int points = plugin.getConfig().getInt("maxPoints");
                        int dropOff = plugin.getConfig().getInt("dropOff");

                        Bukkit.broadcastMessage(ChatColor.STRIKETHROUGH + "----------------------------------------");
                        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "How the Game is Scored:");
                        Bukkit.broadcastMessage("For each item, the following points will be awarded to the player who crafts the item:" +
                                "\nFirst Team: " + ChatColor.GOLD + (int)(points * multiplier) + ChatColor.RESET + " points" +
                                "\nSecond Team: " + ChatColor.GOLD + (int)((points-dropOff) * multiplier) + ChatColor.RESET + " points" +
                                "\nThird Team: " + ChatColor.GOLD + (int)((points-dropOff * 2) * multiplier) + ChatColor.RESET + " points" +
                                "\nFourth Team: " + ChatColor.GOLD + (int)((points-dropOff * 3) * multiplier) + ChatColor.RESET + " points" +
                                "\nFifth Team: " + ChatColor.GOLD + (int)((points-dropOff * 4) * multiplier) + ChatColor.RESET + " points" +
                                //"\nSixth Team: " + ChatColor.GOLD + (int)((points-dropOff * 5) * multiplier) + ChatColor.RESET + " points" +
                                "\nNo points will be awarded past the fifth team!");
                        Bukkit.broadcastMessage(ChatColor.STRIKETHROUGH + "----------------------------------------");

                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        }
                    }

                    if(t.getSecondsLeft() <= 4) {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F);
                            p.sendTitle(ChatColor.GREEN + ">" + t.getSecondsLeft() + "<", "", 2, 16, 2);
                        }
                    }
                    Bingo.timeVar = t.getSecondsLeft();
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        pregameTimer.scheduleTimer();
    }

    public void gameTimer() {
        Countdown gameTimer = new Countdown((JavaPlugin)plugin,
                //should be 60
                plugin.getConfig().getInt("GameTime"),
                //Timer Start
                () -> {
                    gameState = State.GAME_ACTIVE;

                },

                //Timer End
                () -> {
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2F);
                        p.sendTitle(ChatColor.GREEN + "Game Over!", "", 2, 36, 2);
                    }
                    postgameTimer();
                },

                //Each Second
                (t) -> {
                    timeVar = t.getSecondsLeft();
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        gameTimer.scheduleTimer();
    }

    public void postgameTimer() {
        Countdown postgameTimer = new Countdown((JavaPlugin)plugin,
                //should be 60
                plugin.getConfig().getInt("PostGameTime"),
                //Timer Start
                () -> {
                    gameState = State.GAME_OVER;
                },

                //Timer End
                () -> {
                    HandlerList.unregisterAll(plugin);
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.teleport(plugin.getConfig().getLocation("lobby"));
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "startRound");
                },

                //Each Second
                (t) -> {
                    timeVar = t.getSecondsLeft();
                    if(t.getSecondsLeft() == t.getTotalSeconds() - 5) {
                        for (Team team : handler.getTeams()) {
                            ArrayList<TempPointsHolder> tempPointsList = new ArrayList<>();
                            for (Player p : team.getOnlinePlayers()) {
                                if (team.getPlayerTempPoints(p) != null) {
                                    tempPointsList.add(team.getPlayerTempPoints(p));
                                }
                            }
                            Collections.sort(tempPointsList, Comparator.comparing(TempPointsHolder::getPoints));
                            Collections.reverse(tempPointsList);

                            for (Player p : team.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Your Team Standings:");
                                p.sendMessage(ChatColor.DARK_BLUE + "------------------------------");
                                int number = 1;
                                for (TempPointsHolder h : tempPointsList) {
                                    p.sendMessage(number + ". " + handler.getPlayerTeam(p).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " + h.getPoints());
                                    number++;
                                }
                                p.sendMessage(ChatColor.DARK_BLUE + "------------------------------\n");
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            }
                        }
                    }
                    if(t.getSecondsLeft() == t.getTotalSeconds() - 10) {
                        ArrayList<TempPointsHolder> tempPointsList = new ArrayList<>();
                        for(Team team: handler.getTeams()) {
                            for(Player p:team.getOnlinePlayers()) {
                                tempPointsList.add(team.getPlayerTempPoints(p));
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            }
                        }
                        Collections.sort(tempPointsList, Comparator.comparing(TempPointsHolder::getPoints));
                        Collections.reverse(tempPointsList);

                        int max;
                        if(tempPointsList.size() >= 5) {
                            max = 4;
                        } else {
                            max = tempPointsList.size() - 1;
                        }

                        Bukkit.broadcastMessage("\n" + ChatColor.RED + "" + ChatColor.BOLD + "Top 5 Players:");
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "------------------------------");
                        int number = 1;
                        for(int i = 0; i <= max; i++) {
                            TempPointsHolder h = tempPointsList.get(i);
                            Bukkit.broadcastMessage(number + ". " + handler.getPlayerTeam(h.getPlayer()).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " +  h.getPoints());
                            number++;
                        }
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "------------------------------");
                    }

                    if(t.getSecondsLeft() == t.getTotalSeconds() - 15) {
                        ArrayList<Team> teamList = handler.getTempPointsSortedList();

                        Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Team Performance:");
                        Bukkit.broadcastMessage(ChatColor.GREEN + "------------------------------");
                        int counter = 1;
                        for(Team team:teamList) {
                            Bukkit.broadcastMessage(counter + ". " + team.color + ChatColor.BOLD +  team.getTeamName() + ChatColor.RESET + " Points: " + team.getTempPoints());
                            counter++;
                        }
                        Bukkit.broadcastMessage(ChatColor.GREEN + "------------------------------");
                        for(Player p: Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        postgameTimer.scheduleTimer();
    }

    public void prepPlayer(Player p) {
        Team team = handler.getPlayerTeam(p);
        if(team == null) return;

        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 24000, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 24000, 0));
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);

        Inventory inv = p.getInventory();
        inv.clear();

        ItemStack bingoCard = new ItemStack(Material.NETHER_STAR);
        ItemMeta bingoCardMeta = bingoCard.getItemMeta();
        bingoCardMeta.setDisplayName("Bingo Card");
        bingoCard.setItemMeta(bingoCardMeta);

        inv.setItem(8, bingoCard);
        inv.setItem(7, new ItemStack(Material.COOKED_BEEF, 64));

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setUnbreakable(true);
        sword.setItemMeta(swordMeta);
        inv.setItem(0, sword);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.setUnbreakable(true);
        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, 2, true);
        pickaxe.setItemMeta(pickaxeMeta);
        inv.setItem(1, pickaxe);

        ItemStack axe = new ItemStack(Material.IRON_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setUnbreakable(true);
        axeMeta.addEnchant(Enchantment.DIG_SPEED, 2, true);
        axe.setItemMeta(axeMeta);
        inv.setItem(2, axe);

        ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta shovelMeta = shovel.getItemMeta();
        shovelMeta.setUnbreakable(true);
        shovelMeta.addEnchant(Enchantment.DIG_SPEED, 2, true);
        shovel.setItemMeta(shovelMeta);
        inv.setItem(3, shovel);

        //ARMOR
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(translateColor(team.color));
        helmetMeta.setUnbreakable(true);
        helmet.setItemMeta(helmetMeta);
        p.getEquipment().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setColor(translateColor(team.color));
        chestplateMeta.setUnbreakable(true);
        chestplate.setItemMeta(chestplateMeta);
        p.getEquipment().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(translateColor(team.color));
        leggingsMeta.setUnbreakable(true);
        leggings.setItemMeta(leggingsMeta);
        p.getEquipment().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(translateColor(team.color));
        bootsMeta.setUnbreakable(true);
        boots.setItemMeta(bootsMeta);
        p.getEquipment().setBoots(boots);
    }

    private Color translateColor(String c) {
        switch (c) {
            case "§2": return Color.GREEN;
            case "§3": return Color.TEAL;
            case "§5": return Color.PURPLE;
            case "§6": return Color.ORANGE;
            case "§7": return Color.fromRGB(145,145,145); //light gray
            case "§8": return Color.GRAY;
            case "§9": return Color.BLUE;
            case "§a": return Color.LIME;
            case "§b": return Color.AQUA;
            case "§c": return Color.RED;
            case "§d": return Color.fromRGB(255,0,255); //magenta
            case "§e": return Color.YELLOW;
            default: return Color.WHITE;
        }
    }


}
