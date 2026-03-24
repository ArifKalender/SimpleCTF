package me.kugelbltz.simpleCTF.game.listeners.matchListeners;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.events.FlagScoreEvent;
import me.kugelbltz.simpleCTF.events.MatchWinEvent;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.util.GeneralUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class PlayerLifecycleListener implements Listener {
    Set<UUID> quitDuringMatch = new HashSet<>();

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // --- Remove player from the ongoing match ---
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        if (!match.isPlayerInMatch(player)) return;
        quitDuringMatch.add(player.getUniqueId());

        // --- Drop the flag item ---
        match.removePlayerFromMatch(player);
        match.getMessageManager().broadcastMessage(SimpleCTF.getInstance().getMM().deserialize(Message.PLAYER_LEFT_TEAM.get().replace("%player%", player.getName())));
    }

    /**
     * Reset player if they left the game during a match
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (quitDuringMatch.contains(player.getUniqueId())) {
            player.teleport(StaticVariables.getSpawn());
            player.getInventory().clear();
            quitDuringMatch.remove(player.getUniqueId());
        }
    }


    /**
     * Sound effects for winning the match
     */
    @EventHandler
    public void onMatchWin(MatchWinEvent event) {
        GeneralUtils.playSoundForGroup(event.getWinners(), Sound.ITEM_GOAT_HORN_SOUND_1, 3F, 1F);
        GeneralUtils.playSoundForGroup(event.getLosers(), Sound.ENTITY_WITHER_AMBIENT, 3F, 0F);
    }

    /**
     * Sound effects for flag scoring
     */
    @EventHandler
    public void onScore(FlagScoreEvent event) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Collection<Player> capturingTeam = match.getPlayers(event.getCapturingTeam());
        Collection<Player> capturedTeam = match.getPlayers(event.getCapturedTeam());
        GeneralUtils.playSoundForGroup(capturingTeam, Sound.BLOCK_BELL_USE, 3F, 0F);
        GeneralUtils.playSoundForGroup(capturedTeam, Sound.BLOCK_BELL_USE, 3F, 2F);
    }
}
