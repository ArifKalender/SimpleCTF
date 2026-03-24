package me.kugelbltz.simpleCTF.commands;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.admin.*;
import me.kugelbltz.simpleCTF.commands.player.CTFJoin;
import me.kugelbltz.simpleCTF.commands.player.CTFLeave;
import me.kugelbltz.simpleCTF.commands.player.CTFScore;
import me.kugelbltz.simpleCTF.model.Message;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class CaptureTheFlag implements CommandExecutor {

    private final static Map<String, CTFCommand> commands = Map.of(
            "join", new CTFJoin(),
            "leave", new CTFLeave(),
            "score", new CTFScore(),
            "reload", new CTFReload(),
            "setflag", new CTFSetFlag(),
            "start", new CTFStart(),
            "stop", new CTFStop(),
            "setspawn", new CTFSetSpawn()
    );

    public static Map<String, CTFCommand> getSubCommands() {
        return commands;
    }

    /**
     * The main command for the plugin
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        MiniMessage mm = SimpleCTF.getInstance().getMM();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize("<red>Only players can execute this command!"));
            return true;
        }
        if (args.length == 0) {
            sendHelpMessage(mm, sender);
            return true;
        }
        String arg = args[0].toLowerCase(Locale.ENGLISH);
        CTFCommand toExecute = commands.get(arg);
        if (toExecute == null) {
            sendHelpMessage(mm, player);
            return true;
        }

        if (!player.hasPermission(toExecute.getPermission()) && !toExecute.getPermission().startsWith("simplectf.player.")) {
            player.sendMessage(mm.deserialize(Message.NO_PERMISSION.get()));
            return true;
        }

        toExecute.execute(player, args);
        return true;
    }

    private void sendHelpMessage(MiniMessage mm, CommandSender sender) {
        sender.sendMessage(mm.deserialize("<gradient:#eb4034:#ff8800>[---- Valid Commands ----]"));

        sender.sendMessage(mm.deserialize("  <#03fce8>/ctf JOIN <red | blue> <green>Lets the user join the given team's queue"));
        sender.sendMessage(mm.deserialize("  <#03fce8>/ctf LEAVE <green>Lets the user leave their team or queue"));
        sender.sendMessage(mm.deserialize("  <#03fce8>/ctf SCORE <green>Views the scores of the ongoing match"));
        sender.sendMessage(mm.deserialize("  <dark_red>/ctf START <red>Allows the admin to forcibly start the current queue's match"));
        sender.sendMessage(mm.deserialize("  <dark_red>/ctf STOP <red>Allows the admin to forcibly stop the ongoing match or clear the queue"));
        sender.sendMessage(mm.deserialize("  <dark_red>/ctf SETFLAG <red | blue> <red>Allows the admin to set the base/flag location for the given team"));
        sender.sendMessage(mm.deserialize("  <dark_red>/ctf RELOAD <red>Allows the admin to reload config.yml"));

        sender.sendMessage(mm.deserialize("<gradient:#eb4034:#ff8800>[---- Valid Commands ----]"));
    }
}
