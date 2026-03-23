package me.kugelbltz.simpleCTF.game.listeners.matchListeners;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.Message;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static me.kugelbltz.simpleCTF.SimpleCTF.getBannerItems;
import static me.kugelbltz.simpleCTF.SimpleCTF.getMM;
import static me.kugelbltz.simpleCTF.util.GeneralUtils.addItem;
import static me.kugelbltz.simpleCTF.util.GeneralUtils.playSoundForGroup;

public class FlagInteractionListener implements Listener {

    /**
     * Prevent block placing for flag items
     */
    @EventHandler
    private void onPlace(PlayerInteractEvent event) {
        ItemStack interactItem = event.getPlayer().getInventory().getItemInMainHand();
        if (getBannerItems().isFlag(interactItem) && event.getAction() == Action.RIGHT_CLICK_BLOCK)
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
        if (getBannerItems().isFlag(item)) {
            match.getFlagManager().setFlagCarrier(event.getItemDrop(), Team.getTeamFromFlag(event.getItemDrop().getItemStack()));
            match.getFlagManager().broadcastFlagDropLocation(Team.getTeamFromFlag(item), player, player.getLocation());
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
        if (!getBannerItems().isFlag(item)) return;
        if (!match.isPlayerInMatch(player)) {
            event.setCancelled(true);
            return;
        }
        Team itemTeam = Team.getTeamFromFlag(item);
        if (itemTeam == Team.NONE) return;
        match.getFlagManager().setFlagCarrier(player, itemTeam);
        match.getMessageManager().broadcastMessage(getMM().deserialize(
                Message.PLAYER_CAUGHT_FLAG.get()
                        .replace("%player%", player.getName())
                        .replace("%color%", itemTeam.name().toUpperCase(Locale.ENGLISH))
        ));
    }

    /**
     * Handle capturing of flags, prevent placing blocks near flags
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
                event.setCancelled(true);
                return;
            }
        }
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        double blueDistance = clickedBlock.getLocation().distance(match.getFlagManager().getFlagLocation(Team.BLUE));
        double redDistance = clickedBlock.getLocation().distance(match.getFlagManager().getFlagLocation(Team.RED));
        if (blueDistance < 3 || redDistance < 3) {
            event.setCancelled(true);
        }
    }


    private void handleFlag(PlayerInteractEvent event, Team flagColor) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        Team playerColor = Team.getTeam(player);
        Team opponent = Team.getOpposite(playerColor);

        if (flagColor != opponent) {
            player.sendMessage(getMM().deserialize(Message.WRONG_BANNER_TEAM.get()));
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
        String message = Message.PLAYER_CAUGHT_FLAG.get().replace("%player%", player.getName()).replace("%color%", capturedTeam.name().toUpperCase(Locale.ENGLISH));
        match.getFlagManager().setFlagCarrier(player, capturedTeam);
        addItem(player, Team.getTeamFlag(capturedTeam));
        Team capturer = Team.getOpposite(capturedTeam);
        playSoundForGroup(match.getPlayers(capturedTeam), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1F, 0F);
        playSoundForGroup(match.getPlayers(capturer), Sound.ENTITY_RAVAGER_CELEBRATE, 1F, 2F);
        match.getMessageManager().broadcastMessage(getMM().deserialize(message));
    }
}
