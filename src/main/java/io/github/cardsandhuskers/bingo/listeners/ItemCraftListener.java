package io.github.cardsandhuskers.bingo.listeners;

import io.github.cardsandhuskers.bingo.handlers.BingoCardHandler;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import static io.github.cardsandhuskers.teams.Teams.handler;

public class ItemCraftListener implements Listener {
    private BingoCardHandler bingoCardHandler;
    public ItemCraftListener(BingoCardHandler bingoCardHandler) {
        this.bingoCardHandler = bingoCardHandler;
    }
    @EventHandler
    public void onItemCraft(CraftItemEvent e) {
        Player p = (Player)e.getWhoClicked();
        if(handler.getPlayerTeam(p) == null) return;
        Material mat = e.getRecipe().getResult().getType();
        bingoCardHandler.updateCollection(handler.getPlayerTeam(p), p, mat);
    }
}
