package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class CTFReload {
    public CTFReload(Player player, String[] ignored) {
        if (!player.hasPermission("simplectf.admin.reload")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        sendConfigMsg(player, "config.yml");
        SimpleCTF.getInstance().reloadConfig();
        sendConfigMsg(player, "MatchMaps.yml");
        SimpleCTF.getInstance().getMatchMapConfig().reloadConfig();
    }
    private void sendConfigMsg(Player player, String cfgName) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PREFIX + "<green>Reloading " + cfgName + "..."));
    }
}
