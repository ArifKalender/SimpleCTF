package me.kugelbltz.simpleCTF.commands;

import me.kugelbltz.simpleCTF.commands.admin.CTFReload;
import me.kugelbltz.simpleCTF.commands.admin.CTFSetFlag;
import me.kugelbltz.simpleCTF.commands.admin.CTFStart;
import me.kugelbltz.simpleCTF.commands.admin.CTFStop;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.commands.player.CTFLeave;
import me.kugelbltz.simpleCTF.commands.player.CTFScore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CaptureTheFlag implements CommandExecutor {

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can execute this command!"));
            return true;
        }
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        String arg = args[0].toUpperCase(Locale.ENGLISH);
        switch (arg) {
            case "JOIN" -> new CTFJoin(player, args);
            case "LEAVE" -> new CTFLeave(player, args);
            case "SCORE" -> new CTFScore(player, args);
            case "START" -> new CTFStart(player, args);
            case "STOP" -> new CTFStop(player, args);
            case "SETFLAG" -> new CTFSetFlag(player, args);
            case "RELOAD" -> new CTFReload(player, args);
            default -> sendHelpMessage(sender);
        }
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {

    }
}
