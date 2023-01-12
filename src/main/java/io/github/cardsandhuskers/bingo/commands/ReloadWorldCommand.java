package io.github.cardsandhuskers.bingo.commands;

import io.github.cardsandhuskers.bingo.Bingo;
import io.github.cardsandhuskers.bingo.handlers.WorldResetHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadWorldCommand implements CommandExecutor {
    Bingo plugin = (Bingo) Bukkit.getPluginManager().getPlugin("Bingo");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        WorldResetHandler worldResetHandler = new WorldResetHandler(plugin);
        worldResetHandler.resetWorld();

        return true;
    }
}
