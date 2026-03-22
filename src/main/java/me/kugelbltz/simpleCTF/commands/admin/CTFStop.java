package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;
import static me.kugelbltz.simpleCTF.util.QueueHandler.*;

public class CTFStop {
    /**
     * Command for forcibly stopping a match or clearing the queue
     */
    public static void execute(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.stop")) {
            player.sendMessage(MM.deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        if (!isMatchRunning) {
            player.sendMessage(MM.deserialize(StaticVariables.PREFIX + "<red>Cleaning current queue..."));
            broadcastMessageToQueue(MM.deserialize(StaticVariables.PREFIX + "<red>Queue interrupted by an admin!"));
            clearQueue();
            return;
        }
        player.sendMessage(MM.deserialize(StaticVariables.PREFIX + "<red>Interrupting current match..."));
        SimpleCTF.getInstance().getCurrentMatch().unloadMatch(StaticVariables.PREFIX + "<red>Match interrupted by an admin!");
    }
}
