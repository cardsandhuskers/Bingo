package io.github.cardsandhuskers.bingo.commands;

import io.github.cardsandhuskers.bingo.Bingo;
import io.github.cardsandhuskers.bingo.handlers.GameStageHandler;
import io.github.cardsandhuskers.bingo.objects.Stats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartGameCommand implements CommandExecutor {
    Bingo plugin;
    private Stats stats;

    public StartGameCommand(Bingo plugin) {
        this.plugin = plugin;
        this.stats = new Stats("Player,Team,Item,itemFinish,Time");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p) {
            if (args.length > 0) {
                try {
                    Bingo.multiplier = Double.parseDouble(args[0]);
                    startGame();
                } catch (Exception e) {
                    p.sendMessage(ChatColor.RED + "ERROR: argument must be a double");
                }
            } else {
                startGame();
            }
        } else {
            if (args.length > 0) {
                try {
                    Bingo.multiplier = Double.parseDouble(args[0]);
                    startGame();
                } catch (Exception e) {
                    System.out.println(ChatColor.RED + "ERROR: argument must be a double");
                }
            } else {
                startGame();
            }
        }

        return true;
    }

    public void startGame() {
        GameStageHandler gameStageHandler = new GameStageHandler(plugin,stats);
        gameStageHandler.startGame();
    }
}
