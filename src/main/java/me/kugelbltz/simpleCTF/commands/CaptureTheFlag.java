package me.kugelbltz.simpleCTF.commands;

import me.kugelbltz.simpleCTF.commands.admin.CTFReload;
import me.kugelbltz.simpleCTF.commands.admin.CTFSetFlag;
import me.kugelbltz.simpleCTF.commands.admin.CTFStart;
import me.kugelbltz.simpleCTF.commands.admin.CTFStop;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.commands.player.CTFLeave;
import me.kugelbltz.simpleCTF.commands.player.CTFScore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;

public class CaptureTheFlag implements CommandExecutor {

    /**
     * The main command for the plugin
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MM.deserialize("<red>Only players can execute this command!"));
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
        sender.sendMessage(MM.deserialize("<gradient:#eb4034:#ff8800>[---- Valid Commands ----]"));

        sender.sendMessage(MM.deserialize("  <#03fce8>/ctf JOIN <red | blue> <green>Lets the user join the given team's queue"));
        sender.sendMessage(MM.deserialize("  <#03fce8>/ctf LEAVE <green>Lets the user leave their team or queue"));
        sender.sendMessage(MM.deserialize("  <#03fce8>/ctf SCORE <green>Views the scores of the ongoing match"));
        sender.sendMessage(MM.deserialize("  <dark_red>/ctf START <red>Allows the admin to forcibly start the current queue's match"));
        sender.sendMessage(MM.deserialize("  <dark_red>/ctf STOP <red>Allows the admin to forcibly stop the ongoing match or clear the queue"));
        sender.sendMessage(MM.deserialize("  <dark_red>/ctf SETFLAG <red | blue> <red>Allows the admin to set the base/flag location for the given team"));
        sender.sendMessage(MM.deserialize("  <dark_red>/ctf RELOAD <red>Allows the admin to reload config.yml"));

        sender.sendMessage(MM.deserialize("<gradient:#eb4034:#ff8800>[---- Valid Commands ----]"));
    }
}
