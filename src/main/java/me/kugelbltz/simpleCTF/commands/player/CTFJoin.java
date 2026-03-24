package me.kugelbltz.simpleCTF.commands.player;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.commands.CTFCommand;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class CTFJoin implements CTFCommand {

    private static void sendHelpMessage(Player player) {
        player.sendMessage(SimpleCTF.getInstance().getMM().deserialize("<red>Invalid command syntax! Correct usage: /ctf join <red|blue>"));
    }

    /**
     * Command for players to join a team
     */
    @Override
    public void execute(Player player, String[] args) {
        MiniMessage mm = SimpleCTF.getInstance().getMM();
        if (args.length != 2) {
            sendHelpMessage(player);
            return;
        }

        Team team;
        try {
            team = Team.valueOf(args[1].toUpperCase(Locale.ENGLISH));
            if (!Team.playableTeams().contains(team)) {
                player.sendMessage(mm.deserialize(Message.INCORRECT_SYNTAX.get()));
                return;
            }
        } catch (IllegalArgumentException ignored) {
            player.sendMessage(mm.deserialize(Message.INCORRECT_SYNTAX.get()));
            return;
        }

        Match match = SimpleCTF.getInstance().getCurrentMatch();
        boolean matchOccupied = match != null;

        if (SimpleCTF.getInstance().getQueueHandler().alreadyInQueue(player)) {
            player.sendMessage(mm.deserialize(Message.ALREADY_IN_QUEUE.get()));
            return;
        }
        if (matchOccupied) {
            player.sendMessage(mm.deserialize(Message.MATCH_OCCUPIED.get()));
            return;
        }

        SimpleCTF.getInstance().getQueueHandler().addToQueue(player, team);
    }


    @Override
    public List<String> getArguments() {
        return List.of("red", "blue");
    }

    @Override
    public String getPermission() {
        return "simplectf.player.join";
    }

}
