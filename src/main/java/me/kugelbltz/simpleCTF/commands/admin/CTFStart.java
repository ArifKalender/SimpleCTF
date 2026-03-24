package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import me.kugelbltz.simpleCTF.util.QueueHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class CTFStart implements CTFCommand {
    /**
     * Command for forcibly starting a match
     */
    @Override
    public void execute(Player player, String[] args) {
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        if (isMatchRunning) { //Cannot start a match if a match is already going on
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.MATCH_OCCUPIED.get()));
            return;
        }
        QueueHandler queueHandler = SimpleCTF.getInstance().getQueueHandler();
        if (!SimpleCTF.getInstance().getQueueHandler().anyoneInQueue()) { // If nobody is in queue can not start a match
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize("<red>There isn't anybody queued in neither red nor blue teams."));
            return;
        }
        new Match().startMatch(queueHandler.getPlayerQueue(Team.RED), queueHandler.getPlayerQueue(Team.BLUE));
        queueHandler.clearQueue();
    }

    @Override
    public List<String> getArguments() {
        return List.of();
    }

    @Override
    public String getPermission() {
        return "simplectf.admin.start";
    }
}
