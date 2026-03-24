package me.kugelbltz.simpleCTF.commands;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public interface CTFCommand {
    void execute(Player player, String[] args);
    List<String> getArguments();
}
