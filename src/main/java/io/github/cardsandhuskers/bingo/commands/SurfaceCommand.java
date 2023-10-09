package io.github.cardsandhuskers.bingo.commands;

import io.github.cardsandhuskers.bingo.Bingo;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class SurfaceCommand implements CommandExecutor {
    private Bingo plugin;

    public SurfaceCommand(Bingo plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player p  && Bingo.gameState == Bingo.State.GAME_ACTIVE) {
            Location location = p.getLocation();
            World w = p.getWorld();

            // Block block = w.getHighestBlockAt((int)location.getX(), (int)location.getZ());
            Block block = w.getHighestBlockAt(location);

            // Location teleportLocation = new Location(w, location.getX(), (double) block.getY() + 1, location.getZ());


            location.setY((double) block.getY() + 1);
            

            return p.teleport(teleportLocation);

            
        } else {
            System.out.println("ERROR: cannot run from console.");
        }

        return false;
    }
}
