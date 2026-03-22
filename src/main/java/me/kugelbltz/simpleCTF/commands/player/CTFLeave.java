package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.game.Match;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getQueueHandler;

// FIXME: SimpleCTF » You are not in a team. when in a match (because list resets when /ctf start is used)
public class CTFLeave implements CTFCommand {

    /**
     * Command for players to leave from a match or their queue
     */
    @Override
    public void execute(Player player, String[] args) {
        getQueueHandler().removePlayer(player, true);
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match != null)
            match.removePlayerFromMatch(player);
    }
}
