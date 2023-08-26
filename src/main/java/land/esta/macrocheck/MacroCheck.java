package land.esta.macrocheck;

import land.esta.macrocheck.listeners.InfoListener;
import land.esta.macrocheck.listeners.PlayerResponseListener;
import land.esta.macrocheck.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MacroCheck extends JavaPlugin {

    private final Map<Player, Set<Block>> cages = new HashMap<>();
    private final Map<Player, BukkitRunnable> timers = new HashMap<>();
    private final Map<Player, Location> previousLocations = new HashMap<>();
    private final Map<Player, Player> executors = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§aMacro Check has began the process of loading...");
        this.getServer().getPluginManager().registerEvents(new PlayerResponseListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InfoListener(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("macrocheck")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cError: Only players are able to execute this command."));
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("&cUsage: /macrocheck <username>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(CC.translate("&cError: &e" + args[0] + "&c is not online."));
                return true;
            }

            executors.put(target, (Player) sender);

            previousLocations.put(target, target.getLocation());
            createCage(target);
            startTimer(target, (Player) sender);

            return true;
        }
        return false;
    }

    public void stopChecking(Player player, boolean passed, Player executor) {
        if (timers.containsKey(player)) {
            timers.get(player).cancel();
            timers.remove(player);

            if (passed) {
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    player.teleport(previousLocations.get(player));
                    removeCage(player);
                    previousLocations.remove(player);
                    executor.sendMessage(CC.translate("&e" + player.getName() + "&a has passed the macro check!"));
                }, 20 * 5);  // 5 seconds delay
            } else {
                removeCage(player);
                previousLocations.remove(player);
            }
        }
        executors.remove(player);
    }

    private void createCage(Player target) {
        Set<Block> cageBlocks = new HashSet<>();
        Location center = target.getLocation().add(0, 20, 0);  // 20 blocks above the player

        int radius = 4;  // radius of the circle, results in a diameter of 8

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);  // distance from the center

                for (int y = 0; y <= 2; y++) {  // 3 blocks high

                    // Check for the floor and the circular wall
                    if (y == 0 || (distance >= radius - 0.5 && distance <= radius + 0.5)) {
                        Block block = center.getWorld().getBlockAt(center.clone().add(x, y, z));
                        cageBlocks.add(block);
                        block.setType(Material.BEDROCK);
                    }
                }
            }
        }

        cages.put(target, cageBlocks);
        Location teleportLocation = center.add(0, 1, 0);
        teleportLocation.setYaw(180);  // Setting the yaw to face north
        target.teleport(teleportLocation);  // teleporting to the middle of the cage facing north
    }

    private void startTimer(Player target, Player executor) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + target.getName() + " 30d Unfair Advantage -s");
                executor.sendMessage(CC.translate("&e" +target.getName() + " &chas failed the macro check."));
                removeCage(target);
                timers.remove(target);
                previousLocations.remove(target);
            }
        };
        runnable.runTaskLater(this, 20 * 30); // 20 ticks * 30 seconds
        timers.put(target, runnable);
    }

    private void removeCage(Player target) {
        if (!cages.containsKey(target)) return;

        for (Block block : cages.get(target)) {
            block.setType(Material.AIR);
        }
        cages.remove(target);
    }

    public boolean isPlayerBeingChecked(Player player) {
        return previousLocations.containsKey(player);
    }

    public Player getExecutor(Player target) {
        return executors.get(target);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§aMacro Check has began the process of unloading...");
        for (Player player : cages.keySet()) {
            removeCage(player);
        }
    }
}