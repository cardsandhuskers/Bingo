package io.github.cardsandhuskers.bingo.objects;

import io.github.cardsandhuskers.bingo.Bingo;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static io.github.cardsandhuskers.teams.Teams.handler;
import static org.bukkit.Bukkit.getScheduler;

public class BingoCard {
    private Player p;
    private HashMap<Team, ArrayList<Material>> teamItemsMap;
    private HashMap<Material, Integer> cardMap;
    private Bingo plugin;
    private final int numPossible;
    private ArrayList<BingoCard> openCards;

    public BingoCard(Bingo plugin, Player p, HashMap<Team, ArrayList<Material>> teamItemsMap, HashMap<Material, Integer> cardMap, ArrayList<BingoCard> openCards) {
        this.plugin = plugin;
        this.p = p;
        numPossible = plugin.getConfig().getInt("numItemsObtainable");
        this.teamItemsMap = teamItemsMap;
        this.cardMap = cardMap;
        this.openCards = openCards;
    }

    public void buildCard() {
        int index = 0;
        Inventory inv = Bukkit.createInventory(p, 45, ChatColor.RED + "Bingo Card");
        for(Material mat:cardMap.keySet()) {
            int numLeft = numPossible - cardMap.get(mat);
            int slot = (index/5 * 9) + (index%5) + 2;
            ItemStack item;

            if(numLeft!= 0) item = new ItemStack(mat, numLeft);
            else item = new ItemStack(Material.BARRIER);

            ItemMeta itemMeta = item.getItemMeta();
            if(teamItemsMap.get(handler.getPlayerTeam(p)).contains(mat)) {
                itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if(numLeft == 0) {
                itemMeta.setDisplayName(mat.toString());
                itemMeta.setLore(Collections.singletonList("You can no longer earn points for this item."));
            }

            item.setItemMeta(itemMeta);
            inv.setItem(slot, item);
            index++;
        }
        p.openInventory(inv);
        getScheduler().scheduleSyncDelayedTask(plugin, ()->{
            if(!openCards.contains(this)) {
                openCards.add(this);
            }
        },1L);

    }
    public Player getPlayer() {
        return p;
    }


}
