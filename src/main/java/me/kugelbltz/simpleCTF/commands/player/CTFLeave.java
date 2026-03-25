package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.util.QueueHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class CTFLeave implements CTFCommand {

    /**
     * Command for players to leave from a match or their queue
     */
    @Override
    public void execute(Player player, String[] args) {
        // Remove player from queue
        MiniMessage mm = SimpleCTF.getInstance().getMM();
        QueueHandler queueHandler = SimpleCTF.getInstance().getQueueHandler();
        if (queueHandler.isInQueue(player)) {
            queueHandler.removePlayer(player, true);
        } else {
            Match match = SimpleCTF.getInstance().getCurrentMatch();
            if (match != null && match.isPlayerInMatch(player)) {
                match.removePlayerFromMatch(player);
                player.sendMessage(mm.deserialize(Message.TEAM_LEAVE.get()));
            } else {
                player.sendMessage(mm.deserialize(Message.NOT_IN_TEAM.get()));
            }
        }
    }

    @Override
    public List<String> getArguments() {
        return List.of();
    }

    @Override
    public String getPermission() {
        return "simplectf.player.leave";
    }
}
