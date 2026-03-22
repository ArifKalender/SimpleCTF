package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import org.bukkit.entity.Player;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;

public class CTFReload implements CTFCommand {
    private static void sendConfigMsg(Player player, String cfgName) {
        player.sendMessage(getMM().deserialize(StaticVariables.PREFIX + "<green>Reloading " + cfgName + "..."));
    }

    /**
     * Command for reloading the config files
     */

    @Override
    public void execute(Player player, String[] ignored) {
        if (!player.hasPermission("simplectf.admin.reload")) {
            player.sendMessage(getMM().deserialize(StaticVariables.NO_PERMISSION));
            return;
        }
        sendConfigMsg(player, "config.yml");
        SimpleCTF.getInstance().reloadConfig();
        StaticVariables.init();
    }
}
