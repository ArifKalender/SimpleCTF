package me.kugelbltz.simpleCTF.game.listeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.events.FlagScoreEvent;
import me.kugelbltz.simpleCTF.events.MatchWinEvent;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import me.kugelbltz.simpleCTF.util.UtilizationMethods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static me.kugelbltz.simpleCTF.SimpleCTF.BANNER_ITEMS;
import static me.kugelbltz.simpleCTF.SimpleCTF.MM;
import static me.kugelbltz.simpleCTF.util.UtilizationMethods.addItem;
import static me.kugelbltz.simpleCTF.util.UtilizationMethods.playSoundForGroup;

public class MatchListener implements Listener {

    Set<UUID> quitDuringMatch = new HashSet<>();

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // --- Remove player from the ongoing match ---
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        if (!match.isPlayerInMatch(player)) return;
        quitDuringMatch.add(player.getUniqueId());
        match.getTeamPlayers(Team.RED).remove(player);
        match.getTeamPlayers(Team.BLUE).remove(player);

        // --- Drop the flag item ---
        UtilizationMethods.dropAllFlags(player);
        match.broadcastMessage(MM.deserialize(StaticVariables.PLAYER_LEFT_TEAM.replaceAll("%player%", player.getName())));
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
     * Drop flags if the player dies during a match
     */
    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        if (!match.isPlayerInMatch(player)) return;
        UtilizationMethods.dropAllFlags(player);
    }

    /**
     * Teleport the player after death to their flag if they're in a match
     */
    @EventHandler
    private void onRespawn(PlayerPostRespawnEvent event){
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        Team team = Team.getTeam(player);
        if (team == Team.NONE) return;
        Bukkit.getScheduler().runTaskLater(SimpleCTF.getInstance(), () -> player.teleport(match.getFlagLocation(team)), 1);
    }

    /**
     * Prevent block placing for flag items
     */
    @EventHandler
    private void onPlace(PlayerInteractEvent event) {
        ItemStack interactItem = event.getPlayer().getInventory().getItemInMainHand();
        if (BANNER_ITEMS.isFlag(interactItem) && event.getAction() == Action.RIGHT_CLICK_BLOCK)
            event.setCancelled(true);
    }

    /**
     * Handle flag drops
     */
    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        Team team = Team.getTeam(player);
        if (BANNER_ITEMS.isFlag(item)) {
            match.setFlagCarrier(event.getItemDrop(), Team.getTeamFromFlag(event.getItemDrop().getItemStack()));
            match.broadcastFlagDropLocation(team, player, player.getLocation());
        }
    }

    /**
     * Handle flag pickups
     */
    @EventHandler
    private void onPickup(PlayerAttemptPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        if (!BANNER_ITEMS.isFlag(item)) return;
        if (!match.isPlayerInMatch(player)) {
            event.setCancelled(true);
            return;
        }
        Team itemTeam = Team.getTeamFromFlag(item);
        if (itemTeam == Team.NONE) return;
        match.setFlagCarrier(player, itemTeam);
        match.broadcastMessage(MM.deserialize(
                StaticVariables.PLAYER_CAUGHT_FLAG
                        .replace("%player%", player.getName())
                        .replace("%color%", itemTeam.name().toUpperCase())
        ));
    }

    /**
     * Handle capturing of flags
     * Prevent placing blocks near flags
     */
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        switch (clickedBlock.getType()) {
            case Material.BLUE_BANNER -> {
                handleFlag(event, Team.BLUE);
                event.setCancelled(true);
                return;
            }
            case Material.RED_BANNER -> {
                handleFlag(event, Team.RED);
                return;
            }
        }
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        double blueDistance = clickedBlock.getLocation().distance(match.getFlagLocation(Team.BLUE));
        double redDistance = clickedBlock.getLocation().distance(match.getFlagLocation(Team.RED));
        if (blueDistance < 3 || redDistance < 3) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent friendly fire
     */
    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;
        boolean sameTeam = Team.getTeam(victim) == Team.getTeam(attacker);
        if (sameTeam) {
            event.setCancelled(true);
            attacker.sendMessage(MM.deserialize(StaticVariables.NO_FRIENDLY_FIRE));
        }
    }

    /**
     * Sound effects for winning the match
     */
    @EventHandler
    private void onMatchWin(MatchWinEvent event) {
        UtilizationMethods.playSoundForGroup(event.getWinners(), Sound.ITEM_GOAT_HORN_SOUND_1, 3F, 1F);
        UtilizationMethods.playSoundForGroup(event.getWinners(), Sound.ENTITY_WITHER_AMBIENT, 3F, 0F);
    }

    /**
     * Sound effects for flag scoring
     */
    @EventHandler
    private void onScore(FlagScoreEvent event) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Set<Player> capturingTeam = match.getTeamPlayers(event.getCapturingTeam());
        Set<Player> capturedTeam = match.getTeamPlayers(event.getCapturedTeam());
        UtilizationMethods.playSoundForGroup(capturingTeam, Sound.BLOCK_BELL_USE, 3F, 0F);
        UtilizationMethods.playSoundForGroup(capturedTeam, Sound.BLOCK_BELL_USE, 3F, 2F);
    }



    private void handleFlag(PlayerInteractEvent event, Team flagColor) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        Team playerColor = Team.getTeam(player);
        Team opponent = Team.getOpposite(playerColor);

        if (flagColor != opponent) {
            player.sendMessage(MM.deserialize(StaticVariables.WRONG_BANNER_TEAM));
            return;
        }
        event.getClickedBlock().setType(Material.AIR);
        captureFlag(player, flagColor, match);
    }

    /**
     * Gives the flag item to player
     * Broadcasts pickups
     * Plays sound effects for teams
     */
    private void captureFlag(Player player, Team capturedTeam, Match match) {
        String message = StaticVariables.PLAYER_CAUGHT_FLAG.replaceAll("%player%", player.getName()).replace("%color%", capturedTeam.name().toUpperCase(Locale.ENGLISH));
        match.setFlagCarrier(player, capturedTeam);
        addItem(player, Team.getTeamFlag(capturedTeam));
        Team capturer = Team.getOpposite(capturedTeam);
        playSoundForGroup(match.getTeamPlayers(capturedTeam), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1F, 0F);
        playSoundForGroup(match.getTeamPlayers(capturer), Sound.ENTITY_RAVAGER_CELEBRATE, 1F, 2F);
        match.broadcastMessage(MM.deserialize(message));
    }

}
