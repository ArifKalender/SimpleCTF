package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;

public class CTFScore implements CTFCommand {

    /**
     * Command for players to see the current scores of the ongoing match
     */
    @Override
    public void execute(Player player, String[] args) {
        Match match = SimpleCTF.getCurrentMatch();
        String msg = Message.CURRENT_SCORE.get();
        if (match == null) {
            msg = msg.replace("%blue_score%", "0").replace("%red_score%", "0");
        } else {
            String blueScore = String.valueOf(match.getScoreManager().getScore(Team.BLUE));
            String redScore = String.valueOf(match.getScoreManager().getScore(Team.RED));
            msg = msg.replace("%blue_score%", blueScore).replace("%red_score%", redScore);
        }
        player.sendMessage(getMM().deserialize(msg));
    }

}
