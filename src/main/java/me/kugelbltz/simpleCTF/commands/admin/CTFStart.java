package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.util.QueueHandler.*;

public class CTFStart implements CTFCommand {
    /**
     * Command for forcibly starting a match
     */
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.start")) {
            player.sendMessage(getMM().deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        if (isMatchRunning) {
            player.sendMessage(getMM().deserialize(StaticVariables.MATCH_OCCUPIED));
            return;
        }
        if (!anyoneInQueue()) {
            player.sendMessage(getMM().deserialize("<red>There isn't anybody queued in neither red nor blue teams."));
            return;
        }
        new Match().startMatch(getPlayerQueue(Team.RED), getPlayerQueue(Team.BLUE));
        clearQueue();
    }
}
