package me.kugelbltz.simpleCTF.game.listeners.matchListeners;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.StaticVariables;
import me.kugelbltz.simpleCTF.game.Match;
import me.kugelbltz.simpleCTF.model.BannerItems;
import me.kugelbltz.simpleCTF.model.Message;
import me.kugelbltz.simpleCTF.model.Team;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static me.kugelbltz.simpleCTF.util.GeneralUtils.addItem;
import static me.kugelbltz.simpleCTF.util.GeneralUtils.playSoundForGroup;

/**
 * Match listener for general flag interaction logic
 */
public class FlagInteractionListener implements Listener {

    /**
     * Prevent block placing for flag items
     */
    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        ItemStack interactItem = event.getPlayer().getInventory().getItemInMainHand();
        if (SimpleCTF.getInstance().getBannerItems().isFlag(interactItem) && event.getAction() == Action.RIGHT_CLICK_BLOCK)
            event.setCancelled(true);
    }

    /**
     * Handle dropped flag items
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item itemEntity = event.getItemDrop();
        ItemStack item = event.getItemDrop().getItemStack();
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        if (SimpleCTF.getInstance().getBannerItems().isFlag(item)) {
            Team itemTeam = BannerItems.getTeamFromFlag(item);
            match.getFlagManager().protectFlagItemEntity(itemEntity);
            match.getFlagManager().setFlagCarrier(itemEntity, itemTeam);
            match.getFlagManager().broadcastFlagDropLocation(itemTeam, player, player.getLocation());
            itemEntity.setUnlimitedLifetime(true);
        }
    }


    /**
     * Handle flag item pickups
     */
    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        Player player = event.getPlayer();
        if (!SimpleCTF.getInstance().getBannerItems().isFlag(item)) return;
        if (!match.isPlayerInMatch(player)) {
            event.setCancelled(true);
            return;
        }
        Team itemTeam = BannerItems.getTeamFromFlag(item);
        if (!Team.playableTeams().contains(itemTeam)) return;
        match.getFlagManager().setFlagCarrier(player, itemTeam);
        match.getMessageManager().broadcastMessage(SimpleCTF.getInstance().getMM().deserialize(
                Message.PLAYER_CAUGHT_FLAG.get()
                        .replace("%player%", player.getName())
                        .replace("%color%", itemTeam.name().toUpperCase(Locale.ENGLISH))
        ));
    }

    /**
     * Handle capturing of flags, prevent placing blocks near flags
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (SimpleCTF.getInstance().getCurrentMatch() == null) return;
        if (clickedBlock == null) return;
        // --- Handle flag capturing ---
        for (Team team : Team.playableTeams()) {
            if (team.getBannerItem() == clickedBlock.getType()) {
                handleFlag(event, team);
                event.setCancelled(true);
            }
        }

        // --- Prevent interacting with blocks near the flag ---
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (match == null) return;
        double blueDistance = clickedBlock.getLocation().distance(match.getFlagManager().getFlagLocation(Team.BLUE));
        double redDistance = clickedBlock.getLocation().distance(match.getFlagManager().getFlagLocation(Team.RED));
        if (blueDistance < StaticVariables.getFlagBaseRadius() || redDistance < StaticVariables.getFlagBaseRadius()) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent flag item destruction
     */
    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;
        ItemStack itemStack = item.getItemStack();
        Team team = BannerItems.getTeamFromFlag(itemStack);
        if (!Team.playableTeams().contains(team)) return;
        event.setCancelled(true);
    }

    /**
     * Prevent flag item destruction
     */
    @EventHandler
    public void onItemDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;
        ItemStack itemStack = item.getItemStack();
        Team team = BannerItems.getTeamFromFlag(itemStack);
        if (!Team.playableTeams().contains(team)) return;
        event.setCancelled(true);
    }

    /**
     * Handles the given flag type for interactions
     */
    private void handleFlag(PlayerInteractEvent event, Team flagColor) {
        Match match = SimpleCTF.getInstance().getCurrentMatch();
        if (flagColor == null || !Team.playableTeams().contains(flagColor)) {
            event.setCancelled(true);
            return;
        }
        if (match == null) return;
        Player player = event.getPlayer();
        Team playerColor = match.getTeam(player);
        if (!Team.playableTeams().contains(playerColor)) return;
        Team opponent = Team.getOpposite(playerColor);

        if (flagColor != opponent) {
            player.sendMessage(SimpleCTF.getInstance().getMM().deserialize(Message.WRONG_BANNER_TEAM.get()));
            event.setCancelled(true);
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
        match.getMessageManager().broadcastMessage(SimpleCTF.getInstance().getMM().deserialize(message));
    }
}
