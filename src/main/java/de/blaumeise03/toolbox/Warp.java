/*
 * Copyright (c) 2020 Blaumeise03
 */

package de.blaumeise03.toolbox;

import de.blaumeise03.spigotUtils.Configuration;
import de.blaumeise03.toolbox.menu.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Warp extends HashMap<World, Location> {
    static List<Warp> warps = new ArrayList<>();
    static Map<World, Menu> menus = new HashMap<>();
    static Configuration warpConfig;
    private String name;
    private ItemStack icon;

    public Warp(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
        ItemMeta iMeta = icon.getItemMeta();
        iMeta.setDisplayName(ChatColor.GOLD + name);
        icon.setItemMeta(iMeta);
    }

    static Warp getWarp(String name) {
        for (Warp warp : warps) {
            if (warp.name.equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    static boolean deleteWorldLocation(World world, String name) {
        Warp warp = getWarp(name);
        if (warp == null) return false;
        return warp.deleteWorldLocation(world);
    }

    static boolean addWarp(Location loc, String name) {
        Warp warp = getWarp(name);
        if (warp == null) {
            warp = new Warp(name, new ItemStack(Material.GRASS_BLOCK));
            warps.add(warp);
        }
        if (warp.hasWorldLocation(loc.getWorld())) return false;
        warp.addWorldLocation(loc.getWorld(), loc);
        saveWarps();
        return true;
    }

    static void reloadWarps() {
        Main.getPlugin().getLogger().info("Loading Warp-config...");
        warps.clear();
        warpConfig.reload();
        for (String warpID : warpConfig.getKeys(false)) {
            String iconName = warpConfig.getString(warpID + ".Icon");
            if (iconName == null) {
                Main.getPlugin().getLogger().warning("Corrupted Warp-Config: Missing Icon for Warp " + warpID);
                iconName = "GRASS_BLOCK";
            }
            Material icon = Material.getMaterial(iconName);
            if (icon == null) {
                Main.getPlugin().getLogger().severe("Material " + iconName + " does not exist (Warp: " + warpID + ")!");
                icon = Material.GRASS_BLOCK;
            }
            Warp warp = new Warp(warpID, new ItemStack(icon));
            ConfigurationSection section = warpConfig.getConfigurationSection(warpID + ".locations");
            for (String worldUUIDS : section.getKeys(false)) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(worldUUIDS);
                } catch (IllegalArgumentException e) {
                    Main.getPlugin().getLogger().severe("Error while loading warps: World-UUID " + worldUUIDS + " is not a valid UUID!");
                    continue;
                }
                World world = Bukkit.getWorld(uuid);
                if (world == null) {
                    Main.getPlugin().getLogger().severe("Error while loading warps: World with UUID " + worldUUIDS + " was not found!");
                    continue;
                }
                double x = warpConfig.getDouble(warpID + ".locations." + worldUUIDS + ".X");
                double y = warpConfig.getDouble(warpID + ".locations." + worldUUIDS + ".Y");
                double z = warpConfig.getDouble(warpID + ".locations." + worldUUIDS + ".Z");
                float yaw = (float) warpConfig.getDouble(warpID + ".locations." + worldUUIDS + ".Yaw");
                float pitch = (float) warpConfig.getDouble(warpID + ".locations." + worldUUIDS + ".Pitch");
                Location loc = new Location(world, x, y, z, yaw, pitch);
                warp.put(world, loc);
            }
            warps.add(warp);
        }

        for (World world : Bukkit.getWorlds()) {
            Menu warpMenu;
            List<MenuChild> children = new ArrayList<>();
            for (Warp warp : warps) {
                if (warp.containsKey(world)) {

                    children.add(new MenuButton(warp.icon, warp.name) {
                        Location loc = warp.get(world);

                        @Override
                        public void onClick(Player p, MenuSession session) {
                            BukkitRunnable runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    p.teleport(loc);
                                    p.sendMessage("§aDu wurdest teleportiert!");
                                    Main.getPlugin().getLogger().info("The Player " + p.getName() + " was teleported to warp " + warp.name + "!");
                                }
                            };
                            runnable.runTaskLater(Main.getPlugin(), 1);
                        }
                    });

                }
            }
            Main.getPlugin().getLogger().warning("Added " + children.size() + " warps to menu for world " + world.getName() + "!");
            warpMenu = new ScrollableMenu(children, 45 - 9);

            warpMenu.rearrange();
            menus.put(world, warpMenu);
        }
        Main.getPlugin().getLogger().info("Warp-config loaded!");
    }

    static void saveWarps() {
        Main.getPlugin().getLogger().info("Saving Warp-config...");
        for (Warp warp : warps) {
            for (Location location : warp.values()) {
                warpConfig.set(warp.name + ".locations." + location.getWorld().getUID().toString() + ".X", location.getX());
                warpConfig.set(warp.name + ".locations." + location.getWorld().getUID().toString() + ".Y", location.getY());
                warpConfig.set(warp.name + ".locations." + location.getWorld().getUID().toString() + ".Z", location.getZ());
                warpConfig.set(warp.name + ".locations." + location.getWorld().getUID().toString() + ".Yaw", location.getYaw());
                warpConfig.set(warp.name + ".locations." + location.getWorld().getUID().toString() + ".Pitch", location.getPitch());
            }
            warpConfig.set(warp.name + ".Icon", warp.icon.getType().name());
        }
        warpConfig.save();
        Main.getPlugin().getLogger().info("Warp-config saved!");
    }

    static boolean doesWarpExist(String name) {
        for (Warp warp : warps) {
            if (warp.name.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    static boolean doesWarpHasWorld(String name, World world) {
        for (Warp warp : warps) {
            if (warp.name.equalsIgnoreCase(name) && warp.containsKey(world)) return true;
        }
        return false;
    }

    static Location getWarpPoint(String name, World world) {
        for (Warp warp : warps) {
            if (warp.name.equalsIgnoreCase(name)) {
                return warp.getOrDefault(world, null);
            }
        }
        return null;
    }

    static void openMenu(Player player) {
        if (!menus.containsKey(player.getWorld())) {
            Main.getPlugin().getLogger().severe("Fatal: Warp-menu for world " + player.getWorld().getName() + " is not available!");
            player.sendMessage("§cEin unerwarteter Fehler ist aufgetreten. Bitte kontaktiere einen Administrator um das Problem zu beheben!");
            return;
        }
        new MenuSession(menus.get(player.getWorld()), player, 45, "§6Warps");
    }

    void addWorldLocation(World world, Location location) {
        this.put(world, location);
    }

    boolean deleteWorldLocation(World world) {
        if (!this.containsKey(world)) return false;
        this.remove(world);
        warpConfig.set(name + ".locations." + world.getUID().toString(), null);
        saveWarps();
        return true;
    }

    private boolean hasWorldLocation(World world) {
        return containsKey(world);
    }

    public String getName() {
        return name;
    }
}
