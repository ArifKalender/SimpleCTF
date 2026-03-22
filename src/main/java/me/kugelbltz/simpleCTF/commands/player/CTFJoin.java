package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.SimpleCTF.getQueueHandler;

public class CTFJoin implements CTFCommand {

    private static void sendHelpMessage(Player player) {
        player.sendMessage(getMM().deserialize("<red>Invalid command syntax! Correct usage: /ctf join <red|blue>"));
    }

    /**
     * Command for players to join a team
     */
    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 2) {
            sendHelpMessage(player);
            return;
        }

        Team team;
        try {
            team = Team.valueOf(args[1].toUpperCase(Locale.ENGLISH));
            if (team == Team.NONE) {
                player.sendMessage(getMM().deserialize(StaticVariables.INCORRECT_SYNTAX));
                return;
            }
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(getMM().deserialize(StaticVariables.INCORRECT_SYNTAX));
            return;
        }

        Match match = SimpleCTF.getInstance().getCurrentMatch();
        boolean matchOccupied = match != null;

        if (getQueueHandler().alreadyInQueue(player)) {
            player.sendMessage(getMM().deserialize(StaticVariables.ALREADY_IN_QUEUE));
            return;
        }
        if (matchOccupied) {
            player.sendMessage(getMM().deserialize(StaticVariables.MATCH_OCCUPIED));
            return;
        }

        getQueueHandler().prepareTeams(player, team);
    }


}
