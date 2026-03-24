package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.model.Message;
import org.bukkit.entity.Player;

import java.util.List;

public class CTFSetSpawn implements CTFCommand {
    @Override
    public void execute(Player player, String[] args) {
        SimpleCTF.getInstance().getConfig().set("Match.Locations.Spawn", player.getLocation());
        player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.PREFIX.get() + "Set the spawn location! Make sure to /ctf reload."));
        SimpleCTF.getInstance().saveConfig();
    }

    @Override
    public List<String> getArguments() {
        return List.of();
    }

    @Override
    public String getPermission() {
        return "simplectf.admin.setspawn";
    }
}
