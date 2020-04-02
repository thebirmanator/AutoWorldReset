package me.thebirmanator.autoworldreset.bukkit.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldResetEvent extends Event {
    private World world;

    private static HandlerList handlers = new HandlerList();

    public WorldResetEvent(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
