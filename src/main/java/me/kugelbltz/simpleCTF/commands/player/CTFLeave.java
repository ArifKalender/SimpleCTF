package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.util.QueueHandler.*;

// FIXME: SimpleCTF » You are not in a team. when in a match (because list resets when /ctf start is used)
public class CTFLeave implements CTFCommand {
    /**
     * Command for players to leave from a match or their queue
     */
    @Override
    public void execute(Player player, String[] args) {
        removePlayer(player, true);
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match != null)
            match.removePlayerFromMatch(player);
    }

    public static void removePlayer(Player player, boolean sendMessageToPlayer) {
        if (!alreadyInQueue(player) && Team.getTeam(player) == Team.NONE) {
            if (sendMessageToPlayer)
                player.sendMessage(getMM().deserialize(StaticVariables.NOT_IN_TEAM));
            return;
        }
        Team team = getQueueTeam(player);
        if (team == Team.NONE) return;
        removePlayerFromQueue(player);
        getPlayerQueue(team).forEach(teamPlayer -> teamPlayer
                .sendMessage(getMM().deserialize(StaticVariables.PLAYER_LEFT_TEAM.replace("%player%", player.getName()))));
        if (sendMessageToPlayer)
            player.sendMessage(getMM().deserialize(StaticVariables.TEAM_LEAVE));
    }
}
