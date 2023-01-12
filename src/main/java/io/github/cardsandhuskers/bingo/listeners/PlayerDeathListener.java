package io.github.cardsandhuskers.bingo.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 24000, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 24000, 0));
    }
}
