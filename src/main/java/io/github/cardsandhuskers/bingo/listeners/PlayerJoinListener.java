package io.github.cardsandhuskers.bingo.listeners;

import io.github.cardsandhuskers.bingo.Bingo;
import io.github.cardsandhuskers.bingo.handlers.GameStageHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

import static io.github.cardsandhuskers.bingo.Bingo.gameState;
import static io.github.cardsandhuskers.teams.Teams.handler;

public class PlayerJoinListener implements Listener {

    private Bingo plugin;
    private GameStageHandler gameStageHandler;

    public PlayerJoinListener(Bingo plugin, GameStageHandler gameStageHandler) {
        this.plugin = plugin;
        this.gameStageHandler = gameStageHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String world = p.getWorld().getName();
        System.out.println(world);
        if(!world.equals(plugin.getConfig().getString("world"))) {
            p.teleport(gameStageHandler.getWorldSpawn());
        }
        if(p.getInventory().getLeggings() == null && gameState == Bingo.State.GAME_ACTIVE) {
            gameStageHandler.prepPlayer(p);
        }

        if(handler.getPlayerTeam(p) == null) {
            //p.teleport(gameStageHandler.getWorldSpawn());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                p.setGameMode(GameMode.SPECTATOR);
            }, 10L);
        }
        /*
        if(logoutLocs.containsKey(p.getUniqueId())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                p.teleport(logoutLocs.get(p.getUniqueId()));
            }, 10L);
        } else {

        }
         */
    }
}
