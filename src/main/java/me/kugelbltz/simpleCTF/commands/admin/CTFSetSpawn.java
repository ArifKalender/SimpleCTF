package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.commands.CTFCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class CTFSetSpawn implements CTFCommand {
    // TODO: Implement
    @Override
    public void execute(Player player, String[] args) {

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
