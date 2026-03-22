package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CTFReload {
    /**
     * Command for reloading the config files
     */
    public void execute(Player player, String[] ignored) {
        if (!player.hasPermission("simplectf.admin.reload")) {
            player.sendMessage(MM.deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        sendConfigMsg(player, "config.yml");
        SimpleCTF.getInstance().reloadConfig();
        StaticVariables.init();
    }

    private void sendConfigMsg(Player player, String cfgName) {
        player.sendMessage(MM.deserialize(StaticVariables.PREFIX + "<green>Reloading " + cfgName + "..."));
    }
}
