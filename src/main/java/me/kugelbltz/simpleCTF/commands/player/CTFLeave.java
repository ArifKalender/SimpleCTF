package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.SimpleCTF.getQueueHandler;

public class CTFLeave implements CTFCommand {

    /**
     * Command for players to leave from a match or their queue
     */
    @Override
    public void execute(Player player, String[] args) {
        // Remove player from queue
        if (getQueueHandler().alreadyInQueue(player)) {
            getQueueHandler().removePlayer(player, true);
            return;
        } else {
            Match match = SimpleCTF.getInstance().getCurrentMatch();
            if (match != null && match.isPlayerInMatch(player)) {
                match.removePlayerFromMatch(player);
                player.sendMessage(getMM().deserialize(StaticVariables.TEAM_LEAVE));
            } else {
                player.sendMessage(getMM().deserialize(StaticVariables.NOT_IN_TEAM));
            }
        }
    }
}
