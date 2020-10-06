package me.thebirmanator.autoworldreset.bukkit.listeners;

import me.thebirmanator.autoworldreset.bukkit.AutoWorldReset;
import me.thebirmanator.autoworldreset.bukkit.events.WorldFinishResetEvent;
import me.thebirmanator.autoworldreset.bukkit.events.WorldResetEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class WorldResetListener implements Listener {
    private AutoWorldReset main;

    public WorldResetListener(AutoWorldReset main) {
        this.main = main;
    }

    @EventHandler
    public void onReset(WorldResetEvent event) {
        // send plugin message to bungee saying that a world is resetting
        AutoWorldReset.getInstance().sendData("startReset", event.getWorld().getName());

        // send players to main world
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(event.getWorld())) {
                player.performCommand("spawn");
            }
        }

        // send titles (Bukkit side)
        Bukkit.getOnlinePlayers().forEach((player) -> {
            player.sendTitle(ChatColor.DARK_PURPLE + "World Reset", ChatColor.LIGHT_PURPLE + event.getWorld().getName(), 20, 40, 20);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("We'll let you know when it's back up!"));
        });

        // get commands before fill
        List<String> cmds = main.getConfig().getStringList(event.getWorld().getName() + ".before-fill-run");
        Plugin wb = Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (wb != null && wb.isEnabled()) {
            // append the fill command to the end of commands to run
            cmds.addAll(main.getConfig().getStringList(event.getWorld().getName() + ".fill-cmds"));
        }

        for (String cmd : cmds) {
            main.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }

        // if there's no fill task to run, wait some time for regen + schematics
        if (wb == null || !wb.isEnabled()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(AutoWorldReset.getInstance(), () -> main.getServer().getPluginManager().callEvent(new WorldFinishResetEvent(event.getWorld())), 400);
        }
    }
}
