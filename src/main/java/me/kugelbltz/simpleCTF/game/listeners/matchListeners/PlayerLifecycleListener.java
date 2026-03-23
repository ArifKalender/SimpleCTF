package me.kugelbltz.simpleCTF.game.listeners.matchListeners;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.Message;
import me.kugelbltz.simpleCTF.events.FlagScoreEvent;
import me.kugelbltz.simpleCTF.events.MatchWinEvent;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.util.GeneralUtils;
import org.bukkit.Bukkit;
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

import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;

public class PlayerLifecycleListener implements Listener {
    Set<UUID> quitDuringMatch = new HashSet<>();

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // --- Remove player from the ongoing match ---
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        if (!match.isPlayerInMatch(player)) return;
        quitDuringMatch.add(player.getUniqueId());
        match.removePlayerFromMatch(player);

        // --- Drop the flag item ---
        GeneralUtils.dropAllFlags(player);
        match.getMessageManager().broadcastMessage(getMM().deserialize(Message.PLAYER_LEFT_TEAM.get().replace("%player%", player.getName())));
        player.getInventory().clear();
    }

    /**
     * Reset player if they left the game during a match
     */
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (quitDuringMatch.contains(player.getUniqueId())) {
            player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
            player.getInventory().clear();
            quitDuringMatch.remove(player.getUniqueId());
        }
    }


    /**
     * Sound effects for winning the match
     */
    @EventHandler
    private void onMatchWin(MatchWinEvent event) {
        GeneralUtils.playSoundForGroup(event.getWinners(), Sound.ITEM_GOAT_HORN_SOUND_1, 3F, 1F);
        GeneralUtils.playSoundForGroup(event.getLosers(), Sound.ENTITY_WITHER_AMBIENT, 3F, 0F);
    }

    /**
     * Sound effects for flag scoring
     */
    @EventHandler
    private void onScore(FlagScoreEvent event) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Collection<Player> capturingTeam = match.getPlayers(event.getCapturingTeam());
        Collection<Player> capturedTeam = match.getPlayers(event.getCapturedTeam());
        GeneralUtils.playSoundForGroup(capturingTeam, Sound.BLOCK_BELL_USE, 3F, 0F);
        GeneralUtils.playSoundForGroup(capturedTeam, Sound.BLOCK_BELL_USE, 3F, 2F);
    }


}
