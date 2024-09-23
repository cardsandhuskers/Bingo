package io.github.cardsandhuskers.bingo.listeners;

import io.github.cardsandhuskers.bingo.Bingo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerRespawnListener implements Listener {
    Bingo plugin;

    public PlayerRespawnListener(Bingo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 24000, 1));
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 24000, 0));
        }, 2L);

    }
}
