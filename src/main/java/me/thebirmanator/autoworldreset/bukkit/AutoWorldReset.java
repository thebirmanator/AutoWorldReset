package me.thebirmanator.autoworldreset.bukkit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.onarandombox.MultiverseCore.MultiverseCore;
import me.thebirmanator.autoworldreset.bukkit.listeners.FillTaskFinishListener;
import me.thebirmanator.autoworldreset.bukkit.listeners.WorldResetListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoWorldReset extends JavaPlugin {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private MultiverseCore mv;
    private ForceResetCmd forceResetCmd = new ForceResetCmd();

    private static AutoWorldReset instance;

    public void onEnable() {
        instance = this;
        mv = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");

        saveDefaultConfig();

        getCommand(forceResetCmd.forcereset).setExecutor(forceResetCmd);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getServer().getPluginManager().registerEvents(new WorldResetListener(this), this);
        getServer().getPluginManager().registerEvents(new FillTaskFinishListener(this), this);

        Set<World> worlds = getResettingWorlds();
        for(World world : worlds) {
            // config info
            ConfigurationSection worldSection = getConfig().getConfigurationSection(world.getName());
            String stringTime = worldSection.getString("next-reset");
            String stringInterval = worldSection.getString("every");

            // converting into useful stuff: reset time/date and time to wait in between
            LocalDateTime resetTime = LocalDateTime.parse(stringTime, formatter);
            int intervalAmount = Integer.parseInt(stringInterval.split(" ")[0]);
            TimeUnit intervalUnits = TimeUnit.valueOf(stringInterval.split(" ")[1]);
            long interval = intervalUnits.toSeconds(intervalAmount);

            // calculating the time until the next reset
            LocalDateTime now = LocalDateTime.now();
            while(Duration.between(now, resetTime).isNegative()) {
                resetTime = resetTime.plusSeconds(interval);
                getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Oh no! It looks like I have passed the scheduled reset time for " + ChatColor.GRAY + world.getName() + ChatColor.LIGHT_PURPLE + "! New reset time: " + resetTime.format(formatter));
            }
            long secondsTillReset = Duration.between(now, resetTime).getSeconds();
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Next reset scheduled for " + ChatColor.GRAY + world.getName() + ChatColor.LIGHT_PURPLE + ": " + resetTime.format(formatter));

            // save the reset time to config
            String stringReset = resetTime.format(formatter);
            worldSection.set("next-reset", stringReset);
            saveConfig();

            // scheduling the resets to run
            ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
            final LocalDateTime finalOldReset = resetTime;
            schedule.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    // saving the new reset time
                    LocalDateTime newResetTime = finalOldReset.plusSeconds(interval);
                    String stringReset = newResetTime.format(formatter);
                    worldSection.set("next-reset", stringReset);
                    saveConfig();
                    getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Starting reset for " + ChatColor.GRAY + world.getName() + ChatColor.LIGHT_PURPLE + "! Next scheduled reset: " + stringReset);

                    // call the reset event. The schedule is async, so this BukkitRunnable must make it sync
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            getServer().getPluginManager().callEvent(new WorldResetEvent(world));
                        }
                    }.runTask(AutoWorldReset.getPlugin(AutoWorldReset.class));
                }
            }, secondsTillReset, interval, TimeUnit.SECONDS);

        }
    }

    public Set<World> getResettingWorlds() {
        Set<World> resetWorlds = new HashSet<>();
        Set<String> worldNames = getConfig().getKeys(false);
        for(String worldName : worldNames) {
            World world = getServer().getWorld(worldName);
            if(world != null) {
                resetWorlds.add(world);
            }
        }
        return resetWorlds;
    }

    public MultiverseCore getMVPlugin() {
        return mv;
    }

    public void sendData(String channel, String worldName) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        // the channel
        output.writeUTF(channel);
        // which world is affected
        output.writeUTF(worldName);
        getServer().sendPluginMessage(this, "BungeeCord", output.toByteArray());
    }

    public static AutoWorldReset getInstance() {
        return instance;
    }
}
