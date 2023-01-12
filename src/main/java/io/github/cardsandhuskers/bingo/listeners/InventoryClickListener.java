package io.github.cardsandhuskers.bingo.listeners;

import io.github.cardsandhuskers.bingo.handlers.BingoCardHandler;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static io.github.cardsandhuskers.teams.Teams.handler;

public class InventoryClickListener implements Listener {
    private BingoCardHandler bingoCardHandler;
    public InventoryClickListener(BingoCardHandler bingoCardHandler) {
        this.bingoCardHandler = bingoCardHandler;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("bingo card")) {
            e.setCancelled(true);
        } else {
            if(e.getCurrentItem() != null) {
                Team t = handler.getPlayerTeam((Player) e.getWhoClicked());
                if(t == null) return;
                bingoCardHandler.updateCollection(t, (Player) e.getWhoClicked(), e.getCurrentItem().getType());
            }
        }
    }
}
