/*
 * Copyright (c) 2020 Blaumeise03
 */

package de.blaumeise03.toolbox;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class NightSkipper {
    static Set<Player> playersInBed = new HashSet<>();

    private NightSkipper() {

    }

    static void trySkip() {
        Set<World> worlds = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isSleeping()) playersInBed.add(p);
            else playersInBed.remove(p);
            worlds.add(p.getWorld());
        }
        for (World world : worlds) {
            if (world.getEnvironment() != World.Environment.NORMAL) continue;
            int playersOnline = world.getPlayers().size();
            int neededPlayers = (int) (playersOnline * 0.5d);
            int sleeping = 0;
            StringBuilder builder = new StringBuilder("Sleeping-players: ");
            for (Player p : playersInBed) {
                builder.append(", ").append(p.getName());
                if (p.getWorld() == world) {
                    builder.append(" W");
                    if (!Main.afkList.containsKey(p) || Main.afkList.get(p) == AfkMode.NONE || Main.afkList.get(p) == AfkMode.MANUEL_AFK) {
                        builder.append(" NA");
                        sleeping++;
                    }
                }
            }
            if (world.getTime() > 12000) {
                Main.getPlugin().getLogger().info("[NightSkipper] " + builder.toString());
                for (Player p : world.getPlayers()) {
                    p.sendMessage("§eEs " + (sleeping > 1 ? "liegen" : "liegt") + "§6 " + sleeping + "§e von §6" + neededPlayers + " Spieler im Bett §2[§c" + playersOnline + " insg.§2]");
                }
                if (sleeping >= neededPlayers) {
                    long time = world.getFullTime();
                    time = time - (time % 24000) + 24000;
                    world.setFullTime(time);
                    Bukkit.broadcastMessage("§eGuten Tag! Es ist Tag §6" + (int) (time / 24000) + "§e!");
                    Main.getPlugin().getLogger().info("Skipping night for World " + world.getName() + ". New Time: " + time);
                }
            }
        }
        /*
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isSleeping()) playersInBed.add(p);
            else playersInBed.remove(p);
        }
        for (Player p : playersInBed) {
            worlds.add(p.getWorld());
        }
        for (World world : worlds) {
            if (world.getEnvironment() != World.Environment.NORMAL) continue;
            int playersOnline = world.getPlayers().size();
            int afk = 0;
            for (Player p : Main.afkList.keySet()) {
                if (p.isOnline() && Main.afkList.get(p).getPriority() > 1) {
                    if (p.getWorld() == world) {
                        playersOnline--;
                        afk++;
                    }
                }
            }
            int sleeping = 0;
            for (Player player : playersInBed) {
                if (player.getWorld() == world) sleeping++;
            }
            if (sleeping > 0 && playersOnline > 0) {
                double percent = (double) sleeping / playersOnline;
                if (world.getTime() > 12000) {
                    for (Player p : world.getPlayers()) {
                        p.sendMessage("§eEs " + (sleeping > 1 ? "liegen" : "liegt") + "§6 " + sleeping + "§e von §6" + playersOnline + " §2[§c" + afk + " afk§2] §a[" + (int) (percent * 1000) / 10D + "%§2/§b50%§a]§e Spieler im Bett.");
                    }
                    if (percent >= 0.5) {
                        long time = world.getFullTime();
                        time = time - (time % 24000) + 25000;
                        world.setFullTime(time);
                        Bukkit.broadcastMessage("§eGuten Tag! Es ist Tag §6" + (int) (time / 24000) + "§e!");
                        Main.getPlugin().getLogger().info("Skipping night for World " + world.getName() + ". New Time: " + time);
                    }
                }
            }
        }*/
    }
}
