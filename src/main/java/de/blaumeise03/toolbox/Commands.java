/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.toolbox;

import de.blaumeise03.spigotUtils.Command;
import de.blaumeise03.spigotUtils.CommandHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.UUID;

public class Commands {
    //public static Menu warpMenu;

    public static void setUp() {
        CommandHandler handler = Main.getPlugin().getHandler();

        new Command(handler, "repair", "Repariert das Item in deiner Hand", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Player p = (Player) sender;
                if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    sender.sendMessage("§cDu musst ein Item in deine Hand halten!");
                    return;
                }
                ItemStack stack = p.getInventory().getItemInMainHand();
                if (!stack.hasItemMeta()) {
                    sender.sendMessage("§cDieses Item hat keine Item-Metadaten, also auch keine Haltbarkeit!");
                    return;
                }
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof Damageable) {
                    ((Damageable) meta).setDamage(0);
                    sender.sendMessage("§aItem repariert!");
                    stack.setItemMeta(meta);
                } else {
                    sender.sendMessage("§cDieses Item hat keine Haltbarkeit!");
                }
            }
        };

        new Command(handler, "speed", "Ändere deine Geschwindigkeit", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    float speed;
                    p.sendMessage("§aDeine alte Laufgeschwindigkeit: " + p.getWalkSpeed() + ". Deine alte Flieggeschwindigkeit: " + p.getFlySpeed() + ".");
                    try {
                        speed = Float.parseFloat(args[0]);
                        if (speed < -1 || speed > 1) {
                            sender.sendMessage("Du musst eine Zahl von -1 bis 1 angeben!");
                            return;
                        }
                        p.setWalkSpeed(speed);
                        p.setFlySpeed(speed);
                        sender.sendMessage("§aGeschwindigkeit auf " + speed + " gesetzt!");
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage("§cDu musst eine Zahl angeben! \"§4" + args[0] + "§c\" ist keine Zahl!");
                    }

                } else {
                    p.setFlySpeed((float) 0.1);
                    p.setWalkSpeed((float) 0.2);
                    p.sendMessage("§aDeine Geschwindigkeit wurde resettet! Wenn du sie erhöhen willst füge die Geschwindigkeit an: /speed <speed>.");
                }
            }
        };

        new Command(handler, "fly", "Schalte zwischen fliegen und laufen um.", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Player p = (Player) sender;
                p.setAllowFlight(!p.getAllowFlight());
                sender.sendMessage("§aDu " + (p.getAllowFlight() ? "§2kannst nun" : "kannst nun§c nicht") + "§a fliegen!");
            }
        };

        new Command(handler, "broadcast", "Überträgt die Nachricht 1 zu 1 an alle Spieler. (Farbcodes werden unterstützt)", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                StringBuilder builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(convertColor(arg)).append(" ");
                }
                Bukkit.broadcastMessage(builder.toString());
                sender.sendMessage(ChatColor.GREEN + "Nachricht gesendet!");
            }
        };

        new Command(handler, "god", "Setzt oder entfernt dich vom GodMode.", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Player p = (Player) sender;
                Set<String> tags = p.getScoreboardTags();
                boolean isGod = false;
                for (String tag : tags)
                    if (tag.equalsIgnoreCase("godMode")) {
                        isGod = true;
                        break;
                    }
                if (!isGod) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 5, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 5, false, false, false));
                    p.addScoreboardTag("godMode");
                    p.sendMessage("§aDu bist nun im §6GodMode§a!");
                } else {
                    p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    p.removePotionEffect(PotionEffectType.SATURATION);
                    p.removeScoreboardTag("godMode");
                    p.sendMessage("§aGodMode§c entfernt§a!");
                }
            }
        };

        new Command(handler, "gm", "Ändere deinen Spielmodus.", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                if (args.length == 1) {
                    Player p = (Player) sender;
                    GameMode mode;
                    switch (args[0].toLowerCase()) {
                        case "0":
                        case "s":
                        case "survival":
                        case "surv":
                            mode = GameMode.SURVIVAL;
                            break;
                        case "1":
                        case "c":
                        case "creative":
                        case "crea":
                            mode = GameMode.CREATIVE;
                            break;
                        case "2":
                        case "spec":
                        case "spectator":
                            mode = GameMode.SPECTATOR;
                            break;
                        case "3":
                        case "a":
                        case "adv":
                        case "adve":
                        case "adventure":
                            mode = GameMode.ADVENTURE;
                            break;
                        default:
                            p.sendMessage("§cUnbekannter modus! Erlaubte: \n§a 0, 1, 2, 3, 4; s, c, spec, adv, creative, spectator, adventure");
                            return;
                    }
                    p.setGameMode(mode);
                    Main.getPlugin().getLogger().warning("Player " + p.getName() + " has changed his gamemode to " + mode.name());
                    p.sendMessage("§aDu bist nun im GameMode §6" + mode.name());
                } else {
                    sender.sendMessage("§cDu musst den gewünschten Spielmodus angeben!");
                }
            }
        };

        new Command(handler, "afk", "Setzt dich in den afk-modus", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Main.setAfk((Player) sender, true);
                sender.sendMessage("§aDu wurdest, wenn du es nicht bereits bist, in den afk-Modus gesetzt!");
                //Main.lastAction.put((Player) sender,0L);
            }
        };

        new Command(handler, "setWarp", "Setzt an deiner Position einen neuen Warp.", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                if (args.length != 1) {
                    sender.sendMessage("§cDu musst einen Namen angeben: §6/setWarp <WarpName>");
                    return;
                }
                boolean success = Warp.addWarp(((Player) sender).getLocation(), args[0]);
                sender.sendMessage((success ? "§aDer Warp wurde erstellt!" : "§cDer Warp existiert bereits!"));
                if (success)
                    Main.getPlugin().getLogger().info("Warp " + args[0] + " was created by " + sender.getName() + "!");
            }
        };

        new Command(handler, "deleteWarp", "Löscht einen Warp", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                if (args.length == 1) {
                    boolean success = Warp.deleteWorldLocation(((Player) sender).getWorld(), args[0]);
                    if (!success) {
                        sender.sendMessage(ChatColor.RED + "Dieser Warp existiert nicht!");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Warp gelöscht!");
                        Main.getPlugin().getLogger().info("Warp " + args[0] + " was deleted in world " + ((Player) sender).getWorld().getName() + " by " + sender.getName() + "!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Bitte gebe einen Warp an!");
                }
            }
        };

        new Command(handler, "listWarps", "Listet alle Warps auf.", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                StringBuilder builder = new StringBuilder(ChatColor.DARK_GREEN + "Alle Warps:");
                for (Warp warp : Warp.warps) {
                    builder.append("\n§6 - ").append(warp.getName());
                }
                sender.sendMessage(builder.toString());
            }
        };

        new Command(handler, "warp", "Teleportiert dich zu dem angegebenen Warp", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    Location warp = Warp.getWarpPoint(args[0], p.getWorld());
                    if (warp == null) {
                        p.sendMessage("§cWarp nicht gefunden! Gebe §6/listWarps§c für eine Liste ein!");
                        return;
                    }
                    p.teleport(warp);
                    p.sendMessage("§aDu wurdest teleportiert!");
                    Main.getPlugin().getLogger().info("The Player " + p.getName() + " was teleported to warp " + args[0] + "!");
                } else {
                    //p.sendMessage("§cDu musst einen Warp angeben: /warp <WarpName>");
                    //new MenuSession(warpMenu, p, 45, "§6Warps");
                    Warp.openMenu(p);
                }
            }
        };

        new Command(handler, "spawn", "Teleportiert dich zum Spawn", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Player p = (Player) sender;
                p.performCommand("warp spawn");
            }
        };

        new Command(handler, "spawnMob", "Spawnt ein oder mehrere Mobs", true, true) {
            @Override
            public void onCommand(String[] args, CommandSender sender, boolean isPlayer, boolean isThirdExecution, CommandSender realSender) {
                Player p = (Player) sender;
                if (args.length == 0) {
                    p.sendMessage("§cZu wenige Argumente! Nutze §6/spawnMob <Mob> [Anzahl]");
                    return;
                }
                int amount = 1;
                if (args.length == 2) {
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                        p.sendMessage("§c\"" + args[1] + "\" ist keine Zahl!");
                        return;
                    }
                }
                Location loc = p.getLocation();
                if (loc.getWorld() == null) {
                    p.sendMessage("§cEin unerwarteter Fehler ist aufgetreten! Deine Position enthält keine Welt!");
                    return;
                }
                EntityType type = null;
                for (EntityType type1 : EntityType.values()) {
                    if (args[0].equalsIgnoreCase(type1.name())) {
                        type = type1;
                        break;
                    }
                }
                if (type == null) {

                    BaseComponent c = new TextComponent("Entity nicht gefunden! Erlaubte Werte: http://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html");
                    c.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html"));
                    c.setColor(net.md_5.bungee.api.ChatColor.RED);
                    p.spigot().sendMessage(c);

                    return;
                }
                UUID ran = UUID.randomUUID();
                if (amount > 20) {
                    //p.sendMessage("§cAchtung! Zu viele mobs können lags verursachen. Mit /kill @e[tag=manuelSpawned] können diese getötet werden.");
                    BaseComponent c = new TextComponent("Achtung! Zu viele Mobs können lags verursachen! ");
                    c.setColor(net.md_5.bungee.api.ChatColor.RED);
                    BaseComponent c1 = new TextComponent("Klicke hier um die Entitäten zu töten! (/kill @e[tag=" + ran.toString() + "]");
                    c1.setColor(net.md_5.bungee.api.ChatColor.RED);
                    c1.setBold(true);
                    c1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kill @e[tag=" + ran.toString() + "]"));
                    c.addExtra(c1);
                    p.spigot().sendMessage(c);
                }
                Integer max = loc.getWorld().getGameRuleValue(GameRule.MAX_ENTITY_CRAMMING);

                if (max == null) max = 24;
                if (amount > max)
                    p.sendMessage("§cWarnung! Die maximale Anzahl von Entitäten wurde erreicht! Es kann sein das die Entitäten Schaden erhalten!");

                for (int i = 0; i < amount; i++) {
                    Entity entity = loc.getWorld().spawnEntity(loc, type);
                    entity.addScoreboardTag(ran.toString());
                    Main.getPlugin().getLogger().info("Spawned Entity " + type.name() + " at position "
                            + loc.getBlockX() + "|"
                            + loc.getBlockY() + "|"
                            + loc.getBlockZ() + " with Tag \"" + ran.toString() + "\"! Spawned by " + p.getName() + "!");
                }
                BaseComponent c1 = new TextComponent("Klicke hier um die Entitäten zu töten! (/kill @e[tag=" + ran.toString() + "]");
                c1.setColor(net.md_5.bungee.api.ChatColor.DARK_GREEN);
                c1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kill @e[tag=" + ran.toString() + "]"));
                p.spigot().sendMessage(c1);
                p.sendMessage("§aEs wurden §6" + amount + "§a Entitäten vom Typ §6" + type.name() + "§a gespawnt!");
            }
        };
    }

    private static String convertColor(String string) {
        string = string.replace("&4", "§4");
        string = string.replaceAll("&c", "§c");
        string = string.replaceAll("&6", "§6");
        string = string.replaceAll("&e", "§e");
        string = string.replaceAll("&2", "§2");
        string = string.replaceAll("&a", "§a");
        string = string.replaceAll("&b", "§b");
        string = string.replaceAll("&3", "§3");
        string = string.replaceAll("&1", "§1");
        string = string.replaceAll("&9", "§9");
        string = string.replaceAll("&d", "§d");
        string = string.replaceAll("&5", "§5");
        string = string.replaceAll("&f", "§f");
        string = string.replaceAll("&7", "§7");
        string = string.replaceAll("&8", "§8");
        string = string.replaceAll("&0", "§0");
        string = string.replaceAll("&l", "§l");
        string = string.replaceAll("&m", "§m");
        string = string.replaceAll("&n", "§n");
        string = string.replaceAll("&o", "§o");
        string = string.replaceAll("&r", "§r");
        string = string.replaceAll("&k", "§k");
        return string;
    }
}
