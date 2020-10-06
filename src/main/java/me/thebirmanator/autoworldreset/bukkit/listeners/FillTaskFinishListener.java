package me.thebirmanator.autoworldreset.bukkit.listeners;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import me.thebirmanator.autoworldreset.bukkit.AutoWorldReset;
import me.thebirmanator.autoworldreset.bukkit.events.WorldFinishResetEvent;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
