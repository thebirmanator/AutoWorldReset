package me.thebirmanator.autoworldreset;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoWorldReset extends JavaPlugin {

    public void onEnable() {

        int resetDay = DayOfWeek.valueOf("TUESDAY").getValue();

        LocalTime resetTime = ZonedDateTime.parse("23:00", DateTimeFormatter.ofPattern("HH:mm")).toLocalTime();

        //getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "day of week: " + dayOfWeek + ", reset day: " + resetDay);
        //getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "day of month: " + dayOfMonth);

        ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
        LocalDateTime now = LocalDateTime.now();
        LocalTime nowTime = now.toLocalTime();
        int daysTillNext = 7 - Math.abs(now.getDayOfWeek().getValue() - resetDay);
        if(daysTillNext == 7) { // is the day in the config
            if(resetTime.compareTo(nowTime) < 0) { // time is before the time of reset
                daysTillNext = 0;
            }
        }
        // set the day to today, then add how many days until the next reset
        LocalDateTime resetDateTime = resetTime.atDate(now.toLocalDate());
        resetDateTime = resetDateTime.plusDays(daysTillNext);

        long delay = Duration.between(now, resetDateTime).getSeconds();
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "seconds delay: " + delay);

        // long delay, long period, TimeUnit time unit
        long daysInSeconds = TimeUnit.DAYS.toSeconds(7);
        schedule.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getServer().getConsoleSender().sendMessage("hey i ran the scheduled task!");
            }
        }, delay, daysInSeconds, TimeUnit.SECONDS);
    }
}
