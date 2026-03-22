package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.util.QueueHandler.*;

public class CTFStop implements CTFCommand {
    /**
     * Command for forcibly stopping a match or clearing the queue
     */
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.stop")) {
            player.sendMessage(getMM().deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        if (!isMatchRunning) {
            player.sendMessage(getMM().deserialize(StaticVariables.PREFIX + "<red>Cleaning current queue..."));
            broadcastMessageToQueue(getMM().deserialize(StaticVariables.PREFIX + "<red>Queue interrupted by an admin!"));
            clearQueue();
            return;
        }
        player.sendMessage(getMM().deserialize(StaticVariables.PREFIX + "<red>Interrupting current match..."));
        SimpleCTF.getInstance().getCurrentMatch().unloadMatch(StaticVariables.PREFIX + "<red>Match interrupted by an admin!");
    }
}
