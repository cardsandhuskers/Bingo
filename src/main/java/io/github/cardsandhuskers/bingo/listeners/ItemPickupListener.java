package io.github.cardsandhuskers.bingo.listeners;

import io.github.cardsandhuskers.bingo.handlers.BingoCardHandler;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import static io.github.cardsandhuskers.teams.Teams.handler;

public class ItemPickupListener implements Listener {
    private BingoCardHandler bingoCardHandler;
    public ItemPickupListener(BingoCardHandler bingoCardHandler) {
        this.bingoCardHandler = bingoCardHandler;
    }
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) return;
        if(handler.getPlayerTeam((Player) e.getEntity()) == null) return;
        Material mat = e.getItem().getItemStack().getType();
        Player p = (Player) e.getEntity();
        if(mat == null) return;
        bingoCardHandler.updateCollection(handler.getPlayerTeam(p), p, mat);
    }
}
