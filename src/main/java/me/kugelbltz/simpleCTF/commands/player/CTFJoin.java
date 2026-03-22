package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import java.util.*;

import static me.kugelbltz.simpleCTF.SimpleCTF.MM;
import static me.kugelbltz.simpleCTF.util.QueueHandler.alreadyInQueue;
import static me.kugelbltz.simpleCTF.util.QueueHandler.prepareTeams;

public class CTFJoin {

    /**
     * Command for players to join a team
     */
    public static void execute(Player player, String[] args) {
        if (args.length != 2){
            sendHelpMessage(player);
            return;
        }

        Team team;
        try {
            team = Team.valueOf(args[1].toUpperCase(Locale.ENGLISH));
            if (team == Team.NONE) {
                player.sendMessage(MM.deserialize(StaticVariables.INCORRECT_SYNTAX));
                return;
            }
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(MM.deserialize(StaticVariables.INCORRECT_SYNTAX));
            return;
        }

        Match match = SimpleCTF.getInstance().getCurrentMatch();
        boolean matchOccupied = match != null;

        if (alreadyInQueue(player)) {
            player.sendMessage(MM.deserialize(StaticVariables.ALREADY_IN_QUEUE));
            return;
        }
        if (matchOccupied) {
            player.sendMessage(MM.deserialize(StaticVariables.MATCH_OCCUPIED));
            return;
        }

        prepareTeams(player, team);
    }
    private static void sendHelpMessage(Player player) {
        player.sendMessage(MM.deserialize("<red>Invalid command syntax! Correct usage: /ctf join <red|blue>"));
    }


}
