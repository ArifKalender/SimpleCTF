package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.SimpleCTF.getQueueHandler;

public class CTFStart implements CTFCommand {
    /**
     * Command for forcibly starting a match
     */
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.start")) {
            player.sendMessage(getMM().deserialize(Message.NO_PERMISSION.get()));
            return;
        }

        boolean isMatchRunning = SimpleCTF.getCurrentMatch() != null;
        if (isMatchRunning) { //Cannot start a match if a match is already going on
            player.sendMessage(getMM().deserialize(Message.MATCH_OCCUPIED.get()));
            return;
        }
        if (!getQueueHandler().anyoneInQueue()) { // If nobody is in queue can not start a match
            player.sendMessage(getMM().deserialize("<red>There isn't anybody queued in neither red nor blue teams."));
            return;
        }
        new Match().startMatch(getQueueHandler().getPlayerQueue(Team.RED), getQueueHandler().getPlayerQueue(Team.BLUE));
        getQueueHandler().clearQueue();
    }
}
