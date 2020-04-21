/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.toolbox;

import de.blaumeise03.blueUtils.AdvancedPlugin;
import de.blaumeise03.blueUtils.Configuration;
import de.blaumeise03.blueUtils.exceptions.ConfigurationNotFoundException;
import de.blaumeise03.toolbox.menu.MenuListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author Blaumeise03
 */
public class Main extends AdvancedPlugin {

    private static AdvancedPlugin plugin;
    static Map<Player, Long> lastAction = new HashMap<>();
    private static final String afkMessage = "§eDer Spieler [§6%s§e] ist nun §c%s§eAfk!";
    static Map<Player, Long> manuelAfkList = new HashMap<>();
    private static final long timeout = 600000; //= 10 minutes.
    static Map<Player, AfkMode> afkList = new HashMap<>();
    private static BukkitRunnable afkChecker;
    private static final boolean kickAfk = true;
    private static Configuration warpConfig;
    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    private static final Map<String, Location> warps = new HashMap<>();
    /**
     * @deprecated Use the <code>Warp</code>-Class instead.
     **/
    @Deprecated
    private static final Map<String, ItemStack> icon = new HashMap<>();

    public static AdvancedPlugin getPlugin() {
        return plugin;
    }

    public static void playerAction(Player p) {
        lastAction.put(p, System.currentTimeMillis());
    }

    public static void setManuelAfk(Player p) {
        AfkMode cMode = afkList.getOrDefault(p, AfkMode.NONE);
        if (cMode.priority > 0) {
            p.sendMessage("§cDu bist bereits als Afk markiert.");
            return;
        }
        manuelAfkList.put(p, System.currentTimeMillis());
        afkList.put(p, AfkMode.MANUEL_AFK);
        Bukkit.broadcastMessage(String.format(afkMessage, p.getDisplayName(), ""));
    }

    public static void updateAfk(Player p) {
        AfkMode cMode = afkList.getOrDefault(p, AfkMode.NONE);
        long timeSinceLastAction = System.currentTimeMillis() - lastAction.getOrDefault(p, 0L);
        //Check if player should get kicked:
        if (timeSinceLastAction > timeout) {
            //Player should be kicked
            if (cMode != AfkMode.FINAL_AFK) {
                afkList.put(p, AfkMode.FINAL_AFK);
            }
            if (cMode.priority <= 0) {
                Bukkit.broadcastMessage(String.format(afkMessage, p.getDisplayName(), ""));
            }
            if (!p.hasPermission("toolbox.afkKickProtection")) {
                plugin.getLogger().info("Player " + p.getDisplayName() + " was kicked because auf inactivity.");
                p.kickPlayer(ChatColor.RED + "Du wurdest wegen zu langer Inaktivität gekickt!");
            }
            return;
        }
        //Check if player should get marked as afk
        if (timeSinceLastAction > timeout / 2) {
            //Player should be marked as afk
            if (cMode != AfkMode.PRE_AFK) {
                afkList.put(p, AfkMode.PRE_AFK);
                if (cMode.priority <= 0) {
                    Bukkit.broadcastMessage(String.format(afkMessage, p.getDisplayName(), ""));
                }
            }
            return;
        }

        //Check if player has moved
        if (timeSinceLastAction < timeout / 2 && cMode.priority > 0) {
            if (cMode == AfkMode.MANUEL_AFK) {
                long timeSinceManuellAfk = System.currentTimeMillis() - manuelAfkList.getOrDefault(p, 0L);
                if (timeSinceLastAction > timeSinceManuellAfk) {
                    return;
                }
            }
            afkList.put(p, AfkMode.NONE);
            Bukkit.broadcastMessage(String.format(afkMessage, p.getDisplayName(), "nicht mehr "));
        }
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
                    updateAfk(p);
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

    @Override
    public boolean onReload() {
        getLogger().warning("Performing config-reload...");
        warpConfig.reload();
        Warp.reloadWarps();
        return true;
    }
}
