/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.toolbox;

import de.blaumeise03.spigotUtils.AdvancedPlugin;
import de.blaumeise03.spigotUtils.Configuration;
import de.blaumeise03.spigotUtils.exceptions.ConfigurationNotFoundException;
import de.blaumeise03.toolbox.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


/**
 * @author Blaumeise03
 */
public class Main extends AdvancedPlugin {

    private static AdvancedPlugin plugin;
    private static List<Player> afkList = new ArrayList<>();
    static Map<Player, Long> lastAction = new HashMap<>();
    private static Map<Player, Long> manuelAfk = new HashMap<>();
    private static long timeout = 600000; //= 10 minutes.
    private static BukkitRunnable afkChecker;
    private static boolean kickAfk = true;
    private static Configuration warpConfig;
    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    private static Map<String, Location> warps = new HashMap<>();
    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    private static Map<String, ItemStack> icon = new HashMap<>();

    public static AdvancedPlugin getPlugin() {
        return plugin;
    }

    public static void playerAction(Player p) {
        lastAction.put(p, System.currentTimeMillis());
    }

    public static boolean shallPlayerAfk(Player p) {
        long lastActionTime = lastAction.getOrDefault(p, (long) -1);
        long timeSinceLastAction = lastActionTime != -1 ? System.currentTimeMillis() - lastActionTime : -1;
        return timeSinceLastAction >= timeout;
    }

    public static void setAfk(Player p, boolean state) {
        if (afkList.contains(p) || manuelAfk.containsKey(p)) {
            if (state) return;
            if (manuelAfk.containsKey(p) && ((System.currentTimeMillis() - lastAction.get(p)) > (System.currentTimeMillis() - manuelAfk.get(p))))
                return;
            afkList.remove(p);
            manuelAfk.remove(p);
            Bukkit.broadcastMessage("§6" + p.getName() + "§e ist nun nicht mehr afk!");
        } else {
            if (!state) return;
            if (!shallPlayerAfk(p)) {
                if (!manuelAfk.containsKey(p)) {
                    manuelAfk.put(p, System.currentTimeMillis());
                    Bukkit.broadcastMessage("§6" + p.getName() + "§e ist nun afk!");
                }
                return;
            }
            afkList.add(p);
            if (!kickAfk || p.hasPermission("toolbox.afkKickProtection"))
                Bukkit.broadcastMessage("§6" + p.getName() + "§e ist nun afk!");
            else {
                getPlugin().getLogger().warning("Kicking " + p.getName() + " because of inactivity!");
                p.kickPlayer("§cDu wurdest wegen zu langer Inaktivität gekickt!");
            }
        }
    }

    public static boolean isAfk(Player p) {
        return afkList.contains(p);
    }

    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    private static void reloadWarps() {
        warps.clear();
        for (String key : warpConfig.getKeys(false)) {
            double x = warpConfig.getDouble(key + ".X");
            double y = warpConfig.getDouble(key + ".Y");
            double z = warpConfig.getDouble(key + ".Z");
            long pitch = warpConfig.getLong(key + ".Pitch");
            long yaw = warpConfig.getLong(key + ".Yaw");
            String world = warpConfig.getString(key + ".World");
            if (world == null) return;
            UUID worldUUID = UUID.fromString(world);
            World world1 = Bukkit.getWorld(worldUUID);
            if (world1 == null) return;
            Location location = new Location(world1, x, y, z, yaw, pitch);
            warps.put(key, location);
            ItemStack stack;
            String type = warpConfig.getString(key + ".Icon");
            if (type == null) {
                stack = new ItemStack(Material.GRASS_BLOCK);
            } else {
                Material material = Material.getMaterial(type);
                if (material != null) {
                    stack = new ItemStack(material);
                } else {
                    stack = new ItemStack(Material.GRASS_BLOCK);
                    Main.getPlugin().getLogger().warning("Error while loading warps: \"" + type + "\" is not a valid Material!");
                }
            }
            ItemMeta itemMeta = stack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName("§6" + key);
                stack.setItemMeta(itemMeta);
            }
            icon.put(key, stack);
        }
    }

    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    private static void saveWarps() {
        for (String warp : warps.keySet()) {
            Location loc = warps.get(warp);
            if (loc.getWorld() == null) continue;
            warpConfig.set(warp + ".X", loc.getX());
            warpConfig.set(warp + ".Y", loc.getY());
            warpConfig.set(warp + ".Z", loc.getZ());
            warpConfig.set(warp + ".Yaw", loc.getYaw());
            warpConfig.set(warp + ".Pitch", loc.getPitch());
            warpConfig.set(warp + ".World", loc.getWorld().getUID().toString());
            ItemStack stack;
            if (icon.containsKey(warp)) {
                stack = icon.get(warp);
            } else {
                stack = new ItemStack(Material.GRASS_BLOCK);
            }
            //if(stack == null) stack = new ItemStack(Material.GRASS_BLOCK);
            warpConfig.set(warp + ".Icon", stack.getType().name());
        }
        warpConfig.save();
    }

    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    public static boolean addWarp(String warp, Location loc) {
        if (warps.containsKey(warp)) return false;
        warps.put(warp, loc);
        saveWarps();
        return true;
    }

    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    static boolean removeWarp(String warp) {
        if (!warps.containsKey(warp)) return false;
        warps.remove(warp);
        warpConfig.set(warp, null);
        saveWarps();
        return true;
    }

    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    public static Map<String, Location> getWarps() {
        return warps;
    }

    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    public static Location getWarp(String warp) {
        return warps.getOrDefault(warp, null);
    }

    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    public static ItemStack getIcon(String warp) {
        ItemStack stack = null;
        if (icon.containsKey(warp))
            stack = icon.get(warp);
        if (stack == null) {
            stack = new ItemStack(Material.GRASS_BLOCK);
        }
        return stack;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        //getLogger().severe("Toolbox by Blaumeise03 | www.blaumeise03.de | github.com/Blaumeise03");
        plugin = this;
        getLogger().info("Registering events...");
        registerEvent(new Listeners());
        registerEvent(new MenuListener());
        getLogger().info("Starting afk-checker...");
        afkChecker = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOnline())
                        setAfk(p, shallPlayerAfk(p));
                }
            }
        };
        afkChecker.runTaskTimer(this, 40, 40);
        getLogger().info("Loading config...");
        warpConfig = new Configuration("warp.yml", this);
        try {
            warpConfig.setup(true);
        } catch (ConfigurationNotFoundException e) {
            e.printStackTrace();
        }
        Warp.warpConfig = warpConfig;
        Warp.reloadWarps();
        getLogger().info("Adding commands...");
        Commands.setUp();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        //getLogger().severe("Toolbox by Blaumeise03 | www.blaumeise03.de | github.com/Blaumeise03");
        afkChecker.cancel();
        //getLogger().info("Saving warps...");
        //saveWarps();
    }
}
