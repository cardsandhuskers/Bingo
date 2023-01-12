package io.github.cardsandhuskers.bingo.objects;


import io.github.cardsandhuskers.bingo.Bingo;
import io.github.cardsandhuskers.teams.objects.Team;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static io.github.cardsandhuskers.bingo.Bingo.gameState;
import static io.github.cardsandhuskers.bingo.Bingo.timeVar;
import static io.github.cardsandhuskers.bingo.handlers.BingoCardHandler.teamItemsMap;
import static io.github.cardsandhuskers.teams.Teams.handler;

public class Placeholder extends PlaceholderExpansion {
    private final Bingo plugin;

    public Placeholder(Bingo plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getIdentifier() {
        return "Bingo";
    }
    @Override
    public String getAuthor() {
        return "cardsandhuskers";
    }
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }


    @Override
    public String onRequest(OfflinePlayer p, String s) {

        if(s.equalsIgnoreCase("timer")) {
            int time = timeVar;
            int mins = time / 60;
            String seconds = String.format("%02d", time - (mins * 60));
            return mins + ":" + seconds;
        }

        if(s.equalsIgnoreCase("timerstage")) {
            switch(gameState) {
                case GAME_STARTING:
                    return "Game Starts";
                case GAME_ACTIVE:
                        return "Game Ends";
                case GAME_OVER:
                    return "Return to Lobby";
                default:
                    return "Game";
            }
        }
        if(s.equalsIgnoreCase("itemsCollected")) {
            Team t = handler.getPlayerTeam((Player) p);
            if(t == null) return 0 + "";
            if(!teamItemsMap.containsKey(t)) {
                return 0 + "";
            }
            return teamItemsMap.get(t).size() + "";
        }

        return null;
    }
}
