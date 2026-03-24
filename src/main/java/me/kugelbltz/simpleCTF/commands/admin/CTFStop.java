package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.util.QueueHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class CTFStop implements CTFCommand {
    /**
     * Command for forcibly stopping a match or clearing the queue
     */
    @Override
    public void execute(Player player, String[] args) {
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        QueueHandler queueHandler = SimpleCTF.getInstance().getQueueHandler();
        if (!isMatchRunning) {
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.PREFIX.get() + "<red>Cleaning current queue..."));
            queueHandler.broadcastMessageToQueue(SimpleCTF.getInstance().getMM().deserialize(Message.PREFIX.get() + "<red>Queue interrupted by an admin!"));
            queueHandler.clearQueue();
            return;
        }
        player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.PREFIX.get() + "<red>Interrupting current match..."));
        SimpleCTF.getInstance().getCurrentMatch().unloadMatch(Message.PREFIX.get() + "<red>Match interrupted by an admin!");
    }

    @Override
    public List<String> getArguments() {
        return List.of();
    }

    @Override
    public String getPermission() {
        return "simplectf.admin.stop";
    }
}
