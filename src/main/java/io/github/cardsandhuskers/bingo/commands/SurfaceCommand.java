package io.github.cardsandhuskers.bingo.commands;

import io.github.cardsandhuskers.bingo.Bingo;
import org.bukkit.Location;
import org.bukkit.Material;
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

    /**
     * Allows users to teleport to the surface of the world during bingo. 
     * Command can only be executed while a bingo game is active.
     * 
     * @param sender : command source/caller
     * @param command : command that was executed
     * @param label : alias of command
     * @param args : command arguments
     * 
     * @returns boolean : if command was successfully executed
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(Bingo.gameState != Bingo.State.GAME_ACTIVE) {
            System.out.println("ERROR: must run command during bingo.");
        } else if(sender instanceof Player p) {
            Location location = p.getLocation();
            World w = p.getWorld();

            Block block = w.getHighestBlockAt(location);

            /*DEBUG */
            // System.out.println("DEBUG: Highest block type: " + block.getType());

            //checking if highest block is air
            if(block.getType() == Material.AIR) return false;

            //updating y coordinate (height)
            location.setY((double) block.getY() + 1);
            
            return p.teleport(location);
        } else {
            System.out.println("ERROR: cannot run from console.");
        }

        return false;
    }
}
