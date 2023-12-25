package io.github.cardsandhuskers.bingo.handlers;

import io.github.cardsandhuskers.bingo.Bingo;
import io.github.cardsandhuskers.bingo.Bingo.State;
import io.github.cardsandhuskers.bingo.objects.BingoCard;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static io.github.cardsandhuskers.bingo.Bingo.*;
import static io.github.cardsandhuskers.teams.Teams.handler;
import io.github.cardsandhuskers.bingo.objects.Stats;

public class BingoCardHandler {
    //lists each team and their items
    public static HashMap<Team, ArrayList<Material>> teamItemsMap;
    //represents number of teams that have completed each item
    private HashMap<Material, Integer> cardMap;
    private Bingo plugin;
    //list of cards
    private ArrayList<BingoCard> openCards;
    private Stats stats;

    public BingoCardHandler(Bingo plugin, Stats stats) {
        this.plugin = plugin;
        openCards = new ArrayList<>();
        this.stats = stats;
    }

    /**
     * initializes the hashmaps
     * teamItemsMap shows what items each team has collected
     * cardMap has the list of items and the number of teams that have collected it
     */
    public void initializeMaps() {
        teamItemsMap = new HashMap<>();
        cardMap = new HashMap<>();
        //build the map for each team
        for(Team t:handler.getTeams()) {
            teamItemsMap.put(t, new ArrayList<>());
        }



        ArrayList<Material> configItems = new ArrayList<>();
        //build the cardMap
        for(String item:plugin.getConfig().getStringList("cardItems")) {
            Material mat;
            try {
                mat = Material.valueOf(item.toUpperCase());
                configItems.add(mat);
            } catch (Exception e) {
                System.out.println("Cannot recognize " + item + " as an item type");
            }
        }


        int max = 25;
        if(configItems.size() < 25) max = configItems.size();

        ArrayList<Material> finalList = new ArrayList<>();
        for(int i = 1; i <= max; i++) {
            Random random = new Random();
            int item = random.nextInt(configItems.size());
            finalList.add(configItems.get(item));
            configItems.remove(item);
        }


        for(Material mat:finalList) {
            cardMap.put(mat, 0);
        }
    }


    public void updateCollection(Team t, Player player, Material mat) {
        if(gameState != State.GAME_ACTIVE) return;
        if(!addItem(t,mat)) {
            return;
        }

        int maxPoints = plugin.getConfig().getInt("maxPoints");
        int dropOff = plugin.getConfig().getInt("dropOff");
        int numItems = plugin.getConfig().getInt("numItemsObtainable");
        int points;

        if(cardMap.get(mat) >= numItems + 1) points = 0;
        else points = maxPoints - (dropOff * (cardMap.get(mat) - 1));

        if(points == 0) {
            Bukkit.broadcastMessage(t.color + t.getTeamName() + ChatColor.GRAY + " has collected the " + ChatColor.BOLD + mat.name() + ChatColor.RESET + ChatColor.GRAY + " but gets no points :(");
            //Player,Team,Item,itemFinish,Time
            String lineEntry = player.getName() + "," + t.getTeamName() + "," + mat.name() + "," + "NoPoints" + "," + Bingo.timeVar;
            stats.addEntry(lineEntry);
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String placement;
                switch(cardMap.get(mat)) {
                    case 1: placement = "1st"; break;
                    case 2: placement = "2nd"; break;
                    case 3: placement = "3rd"; break;
                    default: placement = cardMap.get(mat) + "th"; break;
                }
                placement += " place";
                if (handler.getPlayerTeam(p) != null && handler.getPlayerTeam(p).equals(t)) {
                    p.sendMessage(ChatColor.GREEN + "Your team has collected the " + ChatColor.RESET + ChatColor.BOLD + mat.name() + ChatColor.RESET +
                                  ChatColor.GREEN + " in " + placement + ChatColor.RESET + " [+" + ChatColor.YELLOW + "" + ChatColor.BOLD + points + ChatColor.RESET + "].");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                } else {
                    p.sendMessage(t.color + t.getTeamName() + ChatColor.GREEN + " has collected the " + ChatColor.RESET + ChatColor.BOLD + mat.name() + ChatColor.RESET +
                            ChatColor.GREEN + " in " + placement + ".");
                }
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                String lineEntry = player.getName() + "," + t.getTeamName() + "," + mat.name() + "," + placement + "," + Bingo.timeVar;
                stats.addEntry(lineEntry);

            }
            //ppAPI assignments have been moved to take place at the very end of the game
            //ppAPI.give(player.getUniqueId(), (int) (points * multiplier));
            t.addTempPoints(player, points * multiplier);
        }
        updateCards();
    }

    /**
     * Makes a new card for the specified player
     * @param p
     */
    public void openCard(Player p) {
        if(handler.getPlayerTeam(p) == null) return;
        BingoCard card = new BingoCard(plugin, p, teamItemsMap, cardMap, openCards);
        card.buildCard();
        openCards.add(card);
    }

    /**
     * removes the card belonging to the player from the list of open cards
     * @param p
     */
    public void closeCard(Player p) {
        for(BingoCard card:openCards) {
            if(card.getPlayer().equals(p)) {
                openCards.remove(card);
                break;
            }
        }
    }

    /**
     * Updates all open cards
     * called if an item is completed
     */
    public void updateCards() {
        ArrayList<BingoCard> tempCardList = new ArrayList<>();

        for (BingoCard c : openCards) {
            tempCardList.add(c);
        }
        //System.out.println(tempCardList);
        for (BingoCard card : tempCardList) {
            if (card == null) continue;
            card.buildCard();
        }
    }

    /**
     * attempts to add the item to the list for the team
     * @param t
     * @param mat
     * @return false if item shuldn't be added, true if it was added
     */
    private boolean addItem(Team t, Material mat) {
        if(!teamItemsMap.containsKey(t)) return false;
        if(!cardMap.containsKey(mat)) return false;
        ArrayList<Material> teamMats = teamItemsMap.get(t);
        if(teamMats.contains(mat)) return false;

        //This means that it's a new item, the team hasn't collected it yet
        teamMats.add(mat);

        cardMap.put(mat, cardMap.get(mat) + 1);
        return true;
    }
}
