package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFStop {
    public CTFStop(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.stop")) {
            player.sendMessage(MM.deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        if (!isMatchRunning) {
            CTFJoin.getBluePlayersUUIDQueue().clear();
            CTFJoin.getRedPlayersUUIDQueue().clear();
            CTFJoin.broadcastMessageToQueue(MM.deserialize(ConfigManager.PREFIX + "<red> Queue interrupted by an admin!"));
            return;
        }
        SimpleCTF.getInstance().getCurrentMatch().unloadMatch(ConfigManager.PREFIX + "<red>Match interrupted by an admin!");
    }
}
