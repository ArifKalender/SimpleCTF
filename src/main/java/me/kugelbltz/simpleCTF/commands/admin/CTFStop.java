package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class CTFStop {
    public CTFStop(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.stop")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        boolean isMatchRunning = SimpleCTF.getInstance().getCurrentMatch() != null;
        if (!isMatchRunning) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>There is no match going on."));
            return;
        }
        SimpleCTF.getInstance().getCurrentMatch().unloadMatch( ConfigManager.PREFIX + "<red>Match interrupted by an admin!");
    }
}
