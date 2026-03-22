package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFReload {
    /**
     * Command for reloading the config files
     */
    public CTFReload(Player player, String[] ignored) {
        if (!player.hasPermission("simplectf.admin.reload")) {
            player.sendMessage(MM.deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        sendConfigMsg(player, "config.yml");
        SimpleCTF.getInstance().reloadConfig();
        ConfigManager.init();
    }

    private void sendConfigMsg(Player player, String cfgName) {
        player.sendMessage(MM.deserialize(ConfigManager.PREFIX + "<green>Reloading " + cfgName + "..."));
    }
}
