package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.commands.CaptureTheFlag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CTFTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        List<String> subCommands = new ArrayList<>(CaptureTheFlag.getSubCommands().keySet());
        if (args.length == 1)
            return subCommands.stream().filter(cmd -> cmd.startsWith(args[0].toUpperCase(Locale.ENGLISH))).toList();
        if (!subCommands.contains(args[0])) return List.of();
        if (args.length == 2)
            return CaptureTheFlag.getSubCommands().get(args[0]).getArguments()
                    .stream()
                    .filter(cmd -> cmd.startsWith(args[1].toUpperCase(Locale.ENGLISH)))
                    .toList();
        return List.of();
    }
}
