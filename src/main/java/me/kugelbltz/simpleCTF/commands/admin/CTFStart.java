package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFStart {
    /**
     * Command for forcibly starting a match
     */
    public CTFStart(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.start")) {
            player.sendMessage(MM.deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        boolean anybodyInQueue = !CTFJoin.getUUIDQueue(Team.BLUE).isEmpty() || !CTFJoin.getUUIDQueue(Team.RED).isEmpty();
        if (isMatchRunning) {
            player.sendMessage(MM.deserialize(StaticVariables.MATCH_OCCUPIED));
            return;
        }
        if (!anybodyInQueue) {
            player.sendMessage(MM.deserialize("<red>There isn't anybody queued in neither red nor blue teams."));
            return;
        }
        new Match(CTFJoin.getPlayerQueue(Team.RED), CTFJoin.getPlayerQueue(Team.BLUE));
        CTFJoin.getUUIDQueue(Team.RED).clear();
        CTFJoin.getUUIDQueue(Team.BLUE).clear();
    }
}
