/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.toolbox;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

import static de.blaumeise03.toolbox.NightSkipper.playersInBed;
import static de.blaumeise03.toolbox.NightSkipper.trySkip;

public class Listeners implements Listener {
    Map<Player, Long> openDelay = new HashMap<>();

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
        openDelay.remove(e.getPlayer());
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
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().hasPermission("toolbox.openIronDoors") && System.currentTimeMillis() - openDelay.getOrDefault(e.getPlayer(), 0L) > 0 && !e.getPlayer().isSneaking()) {
            if (e.getClickedBlock() != null &&
                    (e.getClickedBlock().getType() == Material.IRON_DOOR || e.getClickedBlock().getType() == Material.IRON_TRAPDOOR)) {
                Openable door = (Openable) e.getClickedBlock().getBlockData();
                door.setOpen(!door.isOpen());
                e.getClickedBlock().setBlockData(door);
                openDelay.put(e.getPlayer(), System.currentTimeMillis() + 150L);
                e.setUseInteractedBlock(Event.Result.DENY);
                if (e.getClickedBlock().getType() == Material.IRON_TRAPDOOR) {
                    if (door.isOpen()) {
                        e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
                    } else {
                        e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                    }
                } else if (e.getClickedBlock().getType() == Material.IRON_DOOR) {
                    if (door.isOpen()) {
                        e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                    } else {
                        e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.DEBUG_STICK && e.getPlayer().isOp()) {
            e.setCancelled(false);
        }
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
