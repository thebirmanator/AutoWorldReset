package me.thebirmanator.autoworldreset.bukkit.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import me.thebirmanator.autoworldreset.bukkit.AutoWorldReset;
import me.thebirmanator.autoworldreset.bukkit.events.WorldFinishResetEvent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.*;
import java.util.List;
import java.util.Set;

public class FillTaskFinishListener implements Listener {

    private AutoWorldReset main;
    public FillTaskFinishListener(AutoWorldReset main) {
        this.main = main;
    }

    @EventHandler
    public void onFinish(WorldBorderFillFinishedEvent event) {
        World world = event.getWorld();
        // send message to Bungee that world has finished generating
        AutoWorldReset.getInstance().sendData("endReset", world.getName());

        // set the vanilla world border
        BorderData pluginBorder = Config.getBorders().get(event.getWorld().getName());
        WorldBorder vanillaBorder = world.getWorldBorder();
        vanillaBorder.setCenter(pluginBorder.getX(), pluginBorder.getZ());
        // vanilla border uses a side length, not a radius. So multiply by two
        // we also want the vanilla border slightly outside the plugin border, so add a couple extra blocks
        vanillaBorder.setSize(pluginBorder.getRadiusX() * 2 + 2);

        main.getServer().getPluginManager().callEvent(new WorldFinishResetEvent(world));
    }
}
