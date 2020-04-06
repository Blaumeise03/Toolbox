/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.toolbox;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Main.playerAction(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Main.playerAction(e.getPlayer());
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
