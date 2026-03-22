package me.kugelbltz.simpleCTF.commands;

import org.bukkit.entity.Player;

public interface CTFCommand {
    void execute(Player player, String[] args);
}
