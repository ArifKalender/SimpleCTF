package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.Message;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.SimpleCTF.getQueueHandler;

public class CTFStop implements CTFCommand {
    /**
     * Command for forcibly stopping a match or clearing the queue
     */
    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.stop")) {
            player.sendMessage(getMM().deserialize(Message.NO_PERMISSION.get()));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getCurrentMatch() != null;
        if (!isMatchRunning) {
            player.sendMessage(getMM().deserialize(Message.PREFIX.get() + "<red>Cleaning current queue..."));
            getQueueHandler().broadcastMessageToQueue(getMM().deserialize(Message.PREFIX.get() + "<red>Queue interrupted by an admin!"));
            getQueueHandler().clearQueue();
            return;
        }
        player.sendMessage(getMM().deserialize(Message.PREFIX.get() + "<red>Interrupting current match..."));
        SimpleCTF.getCurrentMatch().unloadMatch(Message.PREFIX.get() + "<red>Match interrupted by an admin!");
    }
}
