package io.github.cardsandhuskers.bingo.handlers;

import io.github.cardsandhuskers.bingo.Bingo;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.api.ChunkyAPI;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.bukkit.Bukkit.getLogger;

public class WorldResetHandler {
    Bingo plugin;
    public WorldResetHandler(Bingo plugin) {
        this.plugin = plugin;
    }

    public void resetWorld() {
        Server server = Bukkit.getServer();
        World world = server.getWorld(plugin.getConfig().getString("world"));
        String worldName = world.getName();

        for(Player p: world.getEntitiesByClass(Player.class)) {
            p.teleport(plugin.getConfig().getLocation("lobby"));//TODO ADD LOBBY COMMAND
        }

        server.unloadWorld(world, true);

        //get folder and delete it
        File worldFolder = world.getWorldFolder();
        try {
            FileUtils.deleteDirectory(worldFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WorldCreator creator = new WorldCreator(worldName);
        Random random = new Random();
        long seed = random.nextLong();
        creator.seed(seed);
        world = server.createWorld(creator);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);

        pregenChunks();
    }

    public void pregenChunks() {
        ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
        int radius = plugin.getConfig().getInt("radius");
        chunky.startTask(plugin.getConfig().getString("world"), "rectangle", 0,0, radius, radius, "concentric");
        chunky.onGenerationComplete(event -> getLogger().info("Generation completed for Bingo"));
    }
}
