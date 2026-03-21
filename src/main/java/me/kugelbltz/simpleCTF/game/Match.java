package me.kugelbltz.simpleCTF.game;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import me.kugelbltz.simpleCTF.model.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static me.kugelbltz.simpleCTF.util.UtilizationMethods.removeFlag;

// TODO: Handle leave/death situations properly
// TODO: Cleanup code xx
//       Team enum instead of string handling
// TODO: 3 score = win
public class Match {
    private Location redFlagLocation, blueFlagLocation;
    private Player redFlagCarrier, blueFlagCarrier;
    private int redScore, blueScore;
    private boolean matchRunning = false;
    private final Set<Player> redPlayers = new HashSet<>();
    private final Set<Player> bluePlayers = new HashSet<>();
    private BossBar bossBar;
    private BukkitTask task;

    public Match(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        boolean canStart = initMatch(redPlayers, bluePlayers);
        if (canStart) this.task = gameLoop();
    }

    private boolean initMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        this.redFlagLocation = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.RedFlag");
        this.blueFlagLocation = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.BlueFlag");
        if (redFlagLocation == null || blueFlagLocation == null) {
            SimpleCTF.getInstance().getLogger().severe("\"Match.Locations.RedFlag\" or \"Match.Locations.BlueFlag was improper or empty. Use /ctf setflag <red|blue> to set locations.");
            broadcastMessage(MiniMessage.miniMessage().deserialize("<red> Match environment was not set properly, therefore your match couldn't start."));
            return false;
        }
        this.redPlayers.addAll(redPlayers);
        this.bluePlayers.addAll(bluePlayers);
        this.redScore = 0;
        this.blueScore = 0;
        this.matchRunning = true;
        this.loadBlocks(true);
        SimpleCTF.getInstance().setCurrentMatch(this);
        initPlayers(true);
        return true;
    }

    private void initPlayers(boolean resetState) {
        redPlayers.forEach(player -> {
            player.teleport(redFlagLocation);
            if (resetState) resetPlayerState(player);
        });
        bluePlayers.forEach(player -> {
            player.teleport(blueFlagLocation);
            if (resetState) resetPlayerState(player);
        });
    }

    private void resetPlayerState(Player player) {
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
    }

    private BukkitTask gameLoop() {
        return new BukkitRunnable() {
            int timeLeft = ConfigManager.MATCH_TIME;

            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0) {
                    unloadMatch(ConfigManager.MATCH_TIME_OUT);
                    this.cancel();
                }
                handleBlue();
                handleRed();
                updateBossBar(timeLeft);
                playFlagAnimation();
                if (getPlayersInMatch() <= 0) unloadMatch("<red>No players left.");
            }
        }.runTaskTimer(SimpleCTF.getInstance(), 0, 20);
    }

    private void playFlagAnimation() {
        // --- Block particles for flags ---
        boolean redAvailable = this.redFlagLocation.getBlock().getType() == Material.RED_BANNER;
        boolean blueAvailable = this.blueFlagLocation.getBlock().getType() == Material.BLUE_BANNER;
        if (redAvailable)
            this.redFlagLocation.getWorld().spawnParticle(Particle.FALLING_DUST, redFlagLocation, 10, 1.5, 1.5, 1.5, Material.RED_CONCRETE_POWDER.createBlockData());
        if (blueAvailable)
            this.blueFlagLocation.getWorld().spawnParticle(Particle.FALLING_DUST, blueFlagLocation, 10, 1.5, 1.5, 1.5, Material.BLUE_CONCRETE_POWDER.createBlockData());

        // --- Player particles for flag carriers ---
        boolean redCarrierAvailable = getRedFlagCarrier() != null;
        boolean blueCarrierAvailable = getBlueFlagCarrier() != null;
        if (redCarrierAvailable) {
            redFlagLocation.getWorld().spawnParticle(Particle.FALLING_DUST, redFlagCarrier.getLocation(), 10, 1, 1, 1, Material.RED_CONCRETE_POWDER.createBlockData());
        }
        if (blueCarrierAvailable) {
            blueFlagCarrier.getWorld().spawnParticle(Particle.FALLING_DUST, blueFlagCarrier.getLocation(), 10, 1, 1, 1, Material.BLUE_CONCRETE_POWDER.createBlockData());
        }
    }

    public void updateBossBar(int timeLeft) {
        String title = "Red score: " + this.redScore + " | Blue score: " + this.blueScore;
        double timeLeftNormalized = timeLeft / (double) ConfigManager.MATCH_TIME;
        if (this.bossBar == null) this.bossBar = Bukkit.createBossBar(title, BarColor.YELLOW, BarStyle.SOLID);
        else this.bossBar.setTitle(title);
        this.bossBar.setProgress(timeLeftNormalized);
        this.redPlayers.forEach(player -> {
            if (!this.bossBar.getPlayers().contains(player)) this.bossBar.addPlayer(player);
        });
        this.bluePlayers.forEach(player -> {
            if (!this.bossBar.getPlayers().contains(player)) this.bossBar.addPlayer(player);
        });
    }

    public void unloadMatch(@Nullable String reason) {
        this.redPlayers.forEach(this::removePlayerFromMatch);
        this.bluePlayers.forEach(this::removePlayerFromMatch);
        broadcastMessage(MiniMessage.miniMessage().deserialize(reason));
        this.redPlayers.clear();
        this.bluePlayers.clear();
        this.redScore = 0;
        this.blueScore = 0;
        this.matchRunning = false;
        this.bossBar.removeAll();
        this.bossBar = null;
        this.loadBlocks(false);
        this.task.cancel();
        SimpleCTF.getInstance().setCurrentMatch(null);
    }

    private void loadBlocks(boolean place) {
        if (place) {
            this.redFlagLocation.getBlock().setType(Material.RED_BANNER);
            this.blueFlagLocation.getBlock().setType(Material.BLUE_BANNER);
        } else {
            this.redFlagLocation.getBlock().setType(Material.AIR);
            this.blueFlagLocation.getBlock().setType(Material.AIR);
        }
    }

    private void handleBlue() {
        for (LivingEntity blueNear : blueFlagLocation.getNearbyLivingEntities(3)) {
            if (!(blueNear instanceof Player player)) continue;
            boolean isRed = redPlayers.contains(player);
            boolean isBlue = bluePlayers.contains(player);
            if (!isRed && !isBlue) continue;
            // If youre carrying your own flag
            if (blueFlagCarrier != null && isBlue && blueFlagCarrier.equals(player)) {
                // Saving own flag
                blueFlagLocation.getBlock().setType(Material.BLUE_BANNER);
                removeFlag(player, Team.BLUE);
                broadcastMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_PLACE_FLAG.replaceAll("%player%", player.getName())));
                continue;
            }
            // If youre carrying red's flag
            if (redFlagCarrier != null && isBlue && redFlagCarrier.equals(player)) {
                this.blueScore++;
                initPlayers(false);
                loadBlocks(true);
                removeFlag(player, Team.RED);
                Component broadcast = MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_RETURN_FLAG.replaceAll("%player%", player.getName()).replaceAll("%opposite_color%", "RED"));
                broadcastMessage(broadcast);
                return;
            }
        }
    }
    private void handleRed() {
        for (LivingEntity redNear : redFlagLocation.getNearbyLivingEntities(3)) {
            if (!(redNear instanceof Player player)) continue;
            boolean isRed = redPlayers.contains(player);
            boolean isBlue = bluePlayers.contains(player);
            if (!isRed && !isBlue) continue;
            // If youre carrying your own flag
            if (redFlagCarrier != null && isRed && redFlagCarrier.equals(player)) {
                // Saving own flag
                redFlagLocation.getBlock().setType(Material.RED_BANNER);
                removeFlag(player, Team.RED);
                broadcastMessage(MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_PLACE_FLAG.replaceAll("%player%", player.getName())));
                continue;
            }
            // If youre carrying blue's flag
            if (blueFlagCarrier != null && isRed && blueFlagCarrier.equals(player)) {
                this.redScore++;
                initPlayers(false);
                loadBlocks(true);
                removeFlag(player, Team.BLUE);
                Component broadcast = MiniMessage.miniMessage().deserialize(ConfigManager.PLAYER_RETURN_FLAG.replaceAll("%player%", player.getName()).replaceAll("%opposite_color%", "BLUE"));
                broadcastMessage(broadcast);
                return;
            }
        }
    }


    public Player getRedFlagCarrier() {
        return this.redFlagCarrier;
    }

    public Player getBlueFlagCarrier() {
        return this.blueFlagCarrier;
    }

    public void setRedFlagCarrier(@Nullable Player redFlagCarrier) {
        this.redFlagCarrier = redFlagCarrier;
    }

    public void setBlueFlagCarrier(@Nullable Player blueFlagCarrier) {
        this.blueFlagCarrier = blueFlagCarrier;
    }

    public Set<Player> getRedPlayers() {
        return redPlayers;
    }

    public Set<Player> getBluePlayers() {
        return bluePlayers;
    }

    public void broadcastMessage(Component component) {
        redPlayers.forEach(player -> {
            player.sendMessage(component);
        });
        bluePlayers.forEach(player -> {
            player.sendMessage(component);
        });
    }

    public boolean isPlayerInMatch(Player player) {
        return redPlayers.contains(player) || bluePlayers.contains(player);
    }

    public int getBlueScore() {
        return blueScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public long getPlayersInMatch() {
        return this.redPlayers.size() + this.bluePlayers.size();
    }

    public void removePlayerFromMatch(Player player) {
        this.redPlayers.remove(player);
        this.bluePlayers.remove(player);
        this.bossBar.removePlayer(player);
        player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
        resetPlayerState(player);
    }

    public Location getRedFlagLocation() {
        return redFlagLocation.clone();
    }

    public Location getBlueFlagLocation() {
        return blueFlagLocation.clone();
    }
}
