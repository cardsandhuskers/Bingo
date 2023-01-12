package io.github.cardsandhuskers.bingo.listeners;

import io.github.cardsandhuskers.bingo.handlers.BingoCardHandler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemClickListener implements Listener {
    private BingoCardHandler bingoCardHandler;

    public ItemClickListener(BingoCardHandler bingoCardHandler) {
        this.bingoCardHandler = bingoCardHandler;
    }
    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        if(e.getItem() != null && e.getItem().getType() == Material.NETHER_STAR) {
            bingoCardHandler.openCard(e.getPlayer());
        }
    }
}
