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
import me.thebirmanator.autoworldreset.bukkit.AutoWorldReset;
import me.thebirmanator.autoworldreset.bukkit.events.WorldFinishResetEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class WorldFinishResetListener implements Listener {

    @EventHandler
    public void onFinish(WorldFinishResetEvent event) {
        AutoWorldReset main = AutoWorldReset.getInstance();
        World world = event.getWorld();
        // paste schematics using worldedit
        if (main.getConfig().isSet(world.getName() + ".paste-schematics")) {
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
        for (String cmd : cmds) {
            main.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }

        // Titles, Bukkit side
        Bukkit.getOnlinePlayers().forEach((player) -> {
            player.sendTitle(ChatColor.DARK_PURPLE + "Reset Complete", ChatColor.LIGHT_PURPLE + world.getName(), 20, 40, 20);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("You may now go back into the world!"));
        });
    }
}
