/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.toolbox;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import static de.blaumeise03.toolbox.NightSkipper.playersInBed;
import static de.blaumeise03.toolbox.NightSkipper.trySkip;

public class Listeners implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Main.playerAction(e.getPlayer());
    }

    @EventHandler
    public void onEnterBed(PlayerBedEnterEvent e) {
        if (e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            playersInBed.add(e.getPlayer());
            Bukkit.broadcastMessage("§6" + e.getPlayer().getDisplayName() + "§e schläft jetzt!");
            new BukkitRunnable() {
                /**
                 * When an object implementing interface <code>Runnable</code> is used
                 * to create a thread, starting the thread causes the object's
                 * <code>run</code> method to be called in that separately executing
                 * thread.
                 * <p>
                 * The general contract of the method <code>run</code> is that it may
                 * take any action whatsoever.
                 *
                 * @see Thread#run()
                 */
                @Override
                public void run() {
                    trySkip();
                }
            }.runTaskLater(Main.getPlugin(), 2);
        }
    }

    @EventHandler
    public void onLeaveBed(PlayerBedLeaveEvent e) {
        playersInBed.remove(e.getPlayer());
        Main.getPlugin().getLogger().info("[NightSkipper] " + e.getPlayer().getDisplayName() + " ist aufgestanden!");
        new BukkitRunnable() {
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                trySkip();
            }
        }.runTaskLater(Main.getPlugin(), 2);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        playersInBed.remove(e.getPlayer());
        new BukkitRunnable() {
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                trySkip();
            }
        }.runTaskLater(Main.getPlugin(), 2);
        Main.afkList.remove(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        if (e.getFrom().getYaw() != e.getTo().getYaw() || e.getFrom().getPitch() != e.getTo().getPitch()) {
            Main.playerAction(e.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Main.playerAction(e.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Main.playerAction(e.getPlayer());
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Main.playerAction((Player) e.getWhoClicked());
        }
    }
}
