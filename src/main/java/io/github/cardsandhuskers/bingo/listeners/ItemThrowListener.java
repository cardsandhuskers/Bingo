package io.github.cardsandhuskers.bingo.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemThrowListener implements Listener {

    @EventHandler
    public void onItemThrow(PlayerDropItemEvent e) {
        if(e.getItemDrop().getItemStack() != null && e.getItemDrop().getItemStack().getType() == Material.NETHER_STAR) {
            e.setCancelled(true);
        }
    }
}
