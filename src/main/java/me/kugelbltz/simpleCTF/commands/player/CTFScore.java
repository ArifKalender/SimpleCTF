package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFScore {

    /**
     * Command for players to see the current scores of the ongoing match
     */
    public CTFScore(Player player, String[] args) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        String msg = ConfigManager.CURRENT_SCORE;
        if (match == null) {
            msg = msg.replaceAll("%blue_score%", "0").replaceAll("%red_score%", "0");
        } else {
            msg = msg.replaceAll("%blue_score%", String.valueOf(match.getBlueScore())).replaceAll("%red_score%", String.valueOf(match.getRedScore()));
        }
        player.sendMessage(MM.deserialize(msg));
    }

}
