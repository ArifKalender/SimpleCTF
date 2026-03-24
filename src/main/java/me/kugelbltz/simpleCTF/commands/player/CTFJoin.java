package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.entity.Player;

import java.util.List;
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
            if (!Team.playableTeams().contains(team)) {
                player.sendMessage(getMM().deserialize(Message.INCORRECT_SYNTAX.get()));
                return;
            }
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(getMM().deserialize(Message.INCORRECT_SYNTAX.get()));
            return;
        }

        Match match = SimpleCTF.getCurrentMatch();
        boolean matchOccupied = match != null;

        if (getQueueHandler().alreadyInQueue(player)) {
            player.sendMessage(getMM().deserialize(Message.ALREADY_IN_QUEUE.get()));
            return;
        }
        if (matchOccupied) {
            player.sendMessage(getMM().deserialize(Message.MATCH_OCCUPIED.get()));
            return;
        }

        getQueueHandler().addToQueue(player, team);
    }


    @Override
    public List<String> getArguments() {
        return List.of("RED", "BLUE");
    }

    @Override
    public String getPermission() {
        return "simplectf.player.join";
    }

}
