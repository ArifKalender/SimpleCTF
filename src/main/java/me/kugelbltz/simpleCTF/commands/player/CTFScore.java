package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFScore {

    /**
     * Command for players to see the current scores of the ongoing match
     */
    public void execute(Player player, String[] args) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        String msg = StaticVariables.CURRENT_SCORE;
        if (match == null) {
            msg = msg.replace("%blue_score%", "0").replace("%red_score%", "0");
        } else {
            msg = msg.replace("%blue_score%", String.valueOf(match.getScore(Team.BLUE))).replace("%red_score%", String.valueOf(match.getScore(Team.RED)));
        }
        player.sendMessage(MM.deserialize(msg));
    }

}
