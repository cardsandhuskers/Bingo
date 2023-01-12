package io.github.cardsandhuskers.bingo.listeners;

import io.github.cardsandhuskers.bingo.handlers.BingoCardHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {
    private BingoCardHandler bingoCardHandler;

    public InventoryCloseListener(BingoCardHandler bingoCardHandler) {
        this.bingoCardHandler = bingoCardHandler;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("bingo card")) {
            bingoCardHandler.closeCard((Player) e.getPlayer());
        }
    }
}
