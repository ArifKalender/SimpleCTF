package me.kugelbltz.simpleCTF.game;

import me.kugelbltz.simpleCTF.SimpleCTF;
import me.kugelbltz.simpleCTF.configuration.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
        initMatch(redPlayers, bluePlayers);
        this.task = gameLoop();
    }

    private void initMatch(Collection<Player> redPlayers, Collection<Player> bluePlayers) {
        this.redFlagLocation = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.RedFlag");
        this.blueFlagLocation = SimpleCTF.getInstance().getConfig().getLocation("Match.Locations.BlueFlag");
        if (redFlagLocation == null || blueFlagLocation == null) {
            SimpleCTF.getInstance().getLogger().severe("\"Match.Locations.RedFlag\" or \"Match.Locations.BlueFlag was improper or empty. Use /ctf setflag <red|blue> to set locations.");
            broadcastMessage(MiniMessage.miniMessage().deserialize("<red> Match environment was not set properly, therefore your match couldn't start."));
            return;
        }
        this.redPlayers.addAll(redPlayers);
        this.bluePlayers.addAll(bluePlayers);
        this.redScore = 0;
        this.blueScore = 0;
        this.matchRunning = true;
        SimpleCTF.getInstance().setCurrentMatch(this);
        initPlayers();
    }

    private void initPlayers() {
        redPlayers.forEach(player -> player.teleport(redFlagLocation));
        bluePlayers.forEach(player -> player.teleport(blueFlagLocation));
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
                updateBossBar(timeLeft);
                playFlagAnimation();
            }
        }.runTaskTimer(SimpleCTF.getInstance(), 0, 20);
    }

    private void playFlagAnimation() {
        // --- Block particles for flags ---
        boolean redAvailable = this.redFlagLocation.getBlock().getType() == Material.RED_BANNER;
        boolean blueAvailable = this.blueFlagLocation.getBlock().getType() == Material.BLUE_BANNER;
        if (redAvailable)
            this.redFlagLocation.getWorld().spawnParticle(Particle.DUST, redFlagLocation, 10, 3, 3, 3, Material.RED_CONCRETE_POWDER);
        if (blueAvailable)
            this.blueFlagLocation.getWorld().spawnParticle(Particle.DUST, redFlagLocation, 10, 3, 3, 3, Material.BLUE_CONCRETE_POWDER);

        // --- Player particles for flag carriers ---
        boolean redCarrierAvailable = getRedFlagCarrier() != null;
        boolean blueCarrierAvailable = getBlueFlagCarrier() != null;
        if (redCarrierAvailable) {
            redFlagLocation.getWorld().spawnParticle(Particle.DUST, redFlagCarrier.getLocation(), 10, 2, 2, 2, Material.RED_CONCRETE_POWDER);
        }
        if (blueCarrierAvailable) {
            blueFlagCarrier.getWorld().spawnParticle(Particle.DUST, blueFlagCarrier.getLocation(), 10, 2, 2, 2, Material.BLUE_CONCRETE_POWDER);
        }
    }

    public void updateBossBar(int timeLeft) {
        String title = "Red score: " + this.redScore + " | Blue score: " + this.blueScore;
        long timeLeftNormalized = ConfigManager.MATCH_TIME / timeLeft;
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
        this.redPlayers.forEach(player -> {
            player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation()); // Assuming that this is the position we want to teleport the player to.
        });
        this.bluePlayers.forEach(player -> {
            player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation()); // Assuming that this is the position we want to teleport the player to.
        });
        broadcastMessage(MiniMessage.miniMessage().deserialize(reason));
        this.redPlayers.clear();
        this.bluePlayers.clear();
        this.redScore = 0;
        this.blueScore = 0;
        this.matchRunning = false;
        this.bossBar.removeAll();
        this.bossBar = null;
        SimpleCTF.getInstance().setCurrentMatch(null);
    }

    private void handleFlagHolders() {
        for (LivingEntity redNear : redFlagLocation.getNearbyLivingEntities(3)) {
            if (!(redNear instanceof Player player)) return;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == SimpleCTF.BANNER_ITEMS.redFlag && redPlayers.contains(player)) {
                player.getInventory().remove(item);
                redFlagLocation.getBlock().setType(Material.RED_BANNER);

                setRedFlagCarrier(null);
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
}
