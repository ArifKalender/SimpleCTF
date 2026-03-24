package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.model.Message;
import org.bukkit.entity.Player;

import java.util.List;


public class CTFReload implements CTFCommand {
    private static void sendConfigMsg(Player player, String cfgName) {
        player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.PREFIX.get() + "<green>Reloading " + cfgName + "..."));
    }

    @Override
    public void execute(Player player, String[] ignored) {
        sendConfigMsg(player, "config.yml");
        SimpleCTF.getInstance().reloadConfig();
        StaticVariables.init();
    }

    @Override
    public List<String> getArguments() {
        return List.of();
    }

    @Override
    public String getPermission() {
        return "simplectf.admin.reload";
    }
}
