package io.github.cardsandhuskers.bingo;

import io.github.cardsandhuskers.bingo.commands.ReloadWorldCommand;
import io.github.cardsandhuskers.bingo.commands.SetLobbyCommand;
import io.github.cardsandhuskers.bingo.commands.StartGameCommand;
import io.github.cardsandhuskers.bingo.objects.Placeholder;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bingo extends JavaPlugin {
    public static double multiplier = 1;
    public static State gameState = State.GAME_STARTING;
    public static int timeVar;
    public static PlayerPointsAPI ppAPI;
    @Override
    public void onEnable() {
        // Plugin startup logic
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            this.ppAPI = PlayerPoints.getInstance().getAPI();
        } else {
            System.out.println("Could not find PlayerPointsAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }


        //Placeholder API validation
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            new Placeholder(this).register();

        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            System.out.println("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }




        getCommand("reloadBingoWorld").setExecutor(new ReloadWorldCommand());
        getCommand("startBingo").setExecutor(new StartGameCommand(this));
        getCommand("setBingoLobby").setExecutor(new SetLobbyCommand(this));

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public enum State {
        GAME_STARTING,
        GAME_ACTIVE,
        GAME_OVER
    }
}
