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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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

        // paste schematics using worldedit
        if(main.getConfig().isSet(world.getName() + ".paste-schematics")) {
            ConfigurationSection schemSection = main.getConfig().getConfigurationSection(world.getName() + ".paste-schematics");
            Set<String> fileNames = schemSection.getKeys(false);
            for (String fileName : fileNames) {
                File spawnFile = new File(WorldEditPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "/schematics/" + fileName + ".schem");
                ClipboardFormat format = ClipboardFormats.findByFile(spawnFile);
                try (ClipboardReader reader = format.getReader(new FileInputStream(spawnFile))) {
                    EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1);
                    Clipboard clipboard = reader.read();
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(session)
                            .to(BlockVector3.at(schemSection.getInt(fileName + ".x"), schemSection.getInt(fileName + ".y"), schemSection.getInt(fileName + ".z")))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                    session.flushSession();
                } catch (IOException | WorldEditException e) {
                    e.printStackTrace();
                }
            }
        }
        // run all the commands for after the fill has finished
        List<String> cmds = main.getConfig().getStringList(world.getName() + ".after-fill-run");
        for(String cmd : cmds) {
            main.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
}
