package me.kugelbltz.simpleCTF.commands.admin;

import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CTFSetFlag {
    // TODO: Implement
    public CTFSetFlag(Player player, String[] args) {
        if (!player.hasPermission("simplectf.admin.setflag")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(ConfigManager.NO_PERMISSION));
            return;
        }
        String color = args[1].toUpperCase(Locale.ENGLISH);
        switch (color) {
            case "RED" -> {

            }
            case "BLUE" -> {

            }
            default -> {

            }
        }
    }
    private void setRedFlag() {

    }
}
