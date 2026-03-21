package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.game.Match;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFStart {
    public CTFStart(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.start")) {
            player.sendMessage(MM.deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        boolean anybodyInQueue = !CTFJoin.getBluePlayersUUIDQueue().isEmpty() || !CTFJoin.getRedPlayersUUIDQueue().isEmpty();
        if (isMatchRunning) {
            player.sendMessage(MM.deserialize(ConfigManager.MATCH_OCCUPIED));
            return;
        }
        if (!anybodyInQueue) {
            player.sendMessage(MM.deserialize("<red>There isn't anybody queued in either red or blue teams."));
            return;
        }
        new Match(CTFJoin.getRedPlayersQueue(), CTFJoin.getBluePlayersQueue());
        CTFJoin.getBluePlayersUUIDQueue().clear();
        CTFJoin.getRedPlayersUUIDQueue().clear();
    }
}
