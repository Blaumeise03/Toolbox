/*
 * Copyright (c) 2019 Blaumeise03
 */

package de.blaumeise03.toolbox;

import de.blaumeise03.blueUtils.command.Command;
import de.blaumeise03.blueUtils.command.CommandHandler;
import de.blaumeise03.blueUtils.command.DefaultCompleter;
import de.blaumeise03.blueUtils.exceptions.CommandNotFoundException;
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
import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Commands {
    //public static Menu warpMenu;

    public static void setUp() {
        CommandHandler handler = Main.getPlugin().getHandler();

        Command repairCmd = new Command("repair", true, true, new Permission("toolbox.repair")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
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

            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args == null || args.length > 1)
                    return null;
                return DefaultCompleter.getPlayerComplete(args.length == 0 ? null : args[0]);
            }
        };
        try {
            handler.addCommand(repairCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command speedCmd = new Command("speed", true, true, new Permission("toolbox.speed")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    float speed;
                    p.sendMessage("§aDeine alte Laufgeschwindigkeit: " + p.getWalkSpeed() + ". Deine alte Fluggeschwindigkeit: " + p.getFlySpeed() + ".");
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

            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args == null || args.length > 1)
                    return null;
                return DefaultCompleter.getPlayerComplete(args.length == 0 ? null : args[0]);
            }
        };
        try {
            handler.addCommand(speedCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command flyCmd = new Command("fly", true, true, new Permission("toolbox.fly")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("get")) {
                        CommandSender s = originalSender;
                        if (!isThird) s = sender;
                        s.sendMessage("§aFly: " + ((Player) sender).getAllowFlight());
                        return;
                    }
                }
                Player p = (Player) sender;
                p.setAllowFlight(!p.getAllowFlight());
                if (isThird) {
                    originalSender.sendMessage("§a" + sender.getName() + " " + (p.getAllowFlight() ? "§2kann nun" : "kann§c nicht mehr") + "§a fliegen!");
                }
                sender.sendMessage("§aDu " + (p.getAllowFlight() ? "§2kannst nun" : "kannst nun§c nicht") + "§a fliegen!");
                Main.getPlugin().getLogger().info("Player " + p.getDisplayName() + " has fly: " + p.getAllowFlight());
            }

            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args != null && args.length > 1)
                    return null;
                List<String> result = DefaultCompleter.getPlayerComplete(args == null || args.length == 0 ? null : args[0]);
                if (args == null || args.length == 0 || args.length == 1 && "get".startsWith(args[0].toLowerCase()))
                    result.add("GET");
                return result;
            }
        };
        try {
            handler.addCommand(flyCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command broadcastCmd = new Command("broadcast", true, false, new Permission("toolbox.broadcast")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                StringBuilder builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(convertColor(arg)).append(" ");
                }
                Bukkit.broadcastMessage(builder.toString());
                sender.sendMessage(ChatColor.GREEN + "Nachricht gesendet!");
            }
        };
        try {
            handler.addCommand(broadcastCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command godCmd = new Command("god", true, true, new Permission("toolbox.godMode")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                Player p = (Player) sender;
                Set<String> tags = p.getScoreboardTags();
                boolean isGod = false;
                for (String tag : tags)
                    if (tag.equalsIgnoreCase("godMode")) {
                        isGod = true;
                        break;
                    }
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("get")) {
                        if (!isThird) originalSender = sender;
                        originalSender.sendMessage("§aGodMode von §2" + sender.getName() + "§a: " + isGod);
                        return;
                    }
                }
                if (!isGod) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 5, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 5, false, false, false));
                    p.addScoreboardTag("godMode");
                    p.sendMessage("§aDu bist nun im §6GodMode§a!");
                    Main.getPlugin().getLogger().info("Player " + p.getDisplayName() + " has now GODMODE");
                } else {
                    p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    p.removePotionEffect(PotionEffectType.SATURATION);
                    p.removeScoreboardTag("godMode");
                    p.sendMessage("§aGodMode§c entfernt§a!");
                    Main.getPlugin().getLogger().info("Removed GODMODE for " + p.getDisplayName());
                }
                if (isThird) {
                    originalSender.sendMessage("§aGodMode of Player §b" + sender.getName() + "§a: §2" + !isGod);
                }
            }

            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args != null && args.length > 1)
                    return null;
                List<String> result = DefaultCompleter.getPlayerComplete(args == null || args.length == 0 ? null : args[0]);
                if (args == null || args.length == 0 || "get".startsWith(args[0].toLowerCase())) result.add("GET");
                return result;
            }
        };
        try {
            handler.addCommand(godCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command gmCmd = new Command("gm", true, false, new Permission("toolbox.gameMode")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
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
                        case "adventure":
                            mode = GameMode.ADVENTURE;
                            break;
                        default:
                            p.sendMessage("§cUnbekannter Modus! Erlaubte: \n§a 0, 1, 2, 3; s, c, spec, adv, survival, creative, spectator, adventure");
                            return;
                    }
                    p.setGameMode(mode);
                    Main.getPlugin().getLogger().info("Player " + p.getName() + " has changed his gamemode to " + mode.name());
                    p.sendMessage("§aDu bist nun im GameMode §6" + mode.name());
                } else {
                    sender.sendMessage("§cDu musst den gewünschten Spielmodus angeben!");
                }
            }
        };
        try {
            handler.addCommand(gmCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command afkCmd = new Command("afk", true, true, new Permission("toolbox.afk")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("list")) {
                        StringBuilder builder = new StringBuilder("§aAlle Spieler die AFK sind:");
                        for (Player p : ((Player) sender).getWorld().getPlayers()) {
                            if (Main.afkList.containsKey(p)) {
                                AfkMode mode = Main.afkList.get(p);
                                if (mode.getPriority() > 0) {
                                    builder.append("\n§2").append(p.getDisplayName()).append("§4: §a").append(mode.name());
                                }
                            }
                        }
                        sender.sendMessage(builder.toString());
                    } else {
                        sender.sendMessage("§cGebe /afk oder /afk list ein.");
                    }
                } else {
                    Main.setManuelAfk((Player) sender);
                }
            }

            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args != null && args.length > 1)
                    return null;
                List<String> result = DefaultCompleter.getPlayerComplete(args == null || args.length == 0 ? null : args[0]);
                if (args == null || args.length == 0 || args.length == 1 && "list".startsWith(args[0].toLowerCase()))
                    result.add("LIST");
                return result;
            }
        };
        try {
            handler.addCommand(afkCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command setWarpCmd = new Command("setWarp", true, false, new Permission("toolbox.warpAdmin")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
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
        try {
            handler.addCommand(setWarpCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }


        Command deleteWarpCmd = new Command("deleteWarp", false, false, new Permission("toolbox.warpAdmin")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
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

            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args != null && args.length > 1)
                    return null;
                List<String> result = new ArrayList<>();
                if (args == null || args.length == 0) {
                    for (Warp warp : Warp.warps) {
                        result.add(warp.getName());
                    }
                } else {
                    for (Warp warp : Warp.warps) {
                        if (warp.getName().toLowerCase().startsWith(args[0].toLowerCase())) result.add(warp.getName());
                    }
                }
                return result;
            }
        };
        try {
            handler.addCommand(deleteWarpCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command listWarpCmd = new Command("listWarps", false, false, new Permission("toolbox.warp")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                StringBuilder builder = new StringBuilder(ChatColor.DARK_GREEN + "Alle Warps:");
                for (Warp warp : Warp.warps) {
                    builder.append("\n§6 - ").append(warp.getName());
                }
                sender.sendMessage(builder.toString());
            }
        };
        try {
            handler.addCommand(listWarpCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command warpCmd = new Command("warp", true, true, new Permission("toolbox.warp")) {

            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    Location warp = Warp.getWarpPoint(args[0], p.getWorld());
                    if (warp == null) {
                        p.sendMessage("§cWarp nicht gefunden! Gebe §6/listWarps§c für eine Liste ein!");
                        return;
                    }
                    p.teleport(warp);
                    p.sendMessage("§aDu wurdest teleportiert!");
                    if (isThird) {
                        originalSender.sendMessage("§2" + sender.getName() + "§a wurde erfolgreich teleportiert!");
                    }
                    Main.getPlugin().getLogger().info("The Player " + p.getName() + " was teleported to warp " + args[0] + "!");
                } else {
                    //p.sendMessage("§cDu musst einen Warp angeben: /warp <WarpName>");
                    //new MenuSession(warpMenu, p, 45, "§6Warps");
                    Warp.openMenu(p);
                }
            }

            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args != null && args.length > 1)
                    return null;
                List<String> result = new ArrayList<>();
                if (args == null || args.length == 0) {
                    for (Warp warp : Warp.warps) {
                        result.add(warp.getName());
                    }
                } else {
                    for (Warp warp : Warp.warps) {
                        if (warp.getName().toLowerCase().startsWith(args[0].toLowerCase())) result.add(warp.getName());
                    }
                }
                return result;
            }
        };
        try {
            handler.addCommand(warpCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command spawnCmd = new Command("spawn", true, true, new Permission("toolbox.spawn")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
                Player p = (Player) sender;
                p.performCommand("warp spawn");
            }
        };
        try {
            handler.addCommand(spawnCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }

        Command spawnMobCmd = new Command("spawnMob", true, true, new Permission("toolbox.spawnMobs")) {
            /**
             * This method is called when a player executes this command
             *
             * @param sender         the {@link CommandSender} who executes the command
             * @param args           the arguments passed to the command
             * @param isPlayer       if the <code>sender</code> is a {@link Player Player}
             * @param isThird        if the command was executed by the <code>originalSender</code> for another player
             *                       e.g: /command player args.. - the command, if {@link Command#isThirdExecutable()} is true,
             *                       gets executed at the 'player' passed as first argument
             * @param originalSender The original sender, equals <code>sender</code> if command was not third-executed
             */
            @Override
            public void execute(CommandSender sender, String[] args, boolean isPlayer, boolean isThird, CommandSender originalSender) {
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
                }
                Main.getPlugin().getLogger().info("Spawned Entity " + type.name() + " at position "
                        + loc.getBlockX() + "|"
                        + loc.getBlockY() + "|"
                        + loc.getBlockZ() + " with Tag \"" + ran.toString() + "\"! Spawned by " + p.getName() + "!");
                BaseComponent c1 = new TextComponent("Klicke hier um die Entitäten zu töten! (/kill @e[tag=" + ran.toString() + "]");
                c1.setColor(net.md_5.bungee.api.ChatColor.DARK_GREEN);
                c1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kill @e[tag=" + ran.toString() + "]"));
                p.spigot().sendMessage(c1);
                p.sendMessage("§aEs wurden §6" + amount + "§a Entitäten vom Typ §6" + type.name() + "§a gespawnt!");
            }

            /**
             * Method for additional tab-arguments.
             *
             * @param args the arguments passed to the command (may contain sub-commands)
             * @return a list with additional arguments for the command
             * @implNote Overwrite this method and let it return all arguments which should be suggested e.g. player names
             */
            @Nullable
            @Override
            public List<String> getAdditionalTabArguments(@Nullable String[] args) {
                if (args != null && args.length > 1)
                    return null;
                List<String> result = new ArrayList<>();
                if (args == null || args.length == 0) {
                    for (EntityType type : EntityType.values()) {
                        result.add(type.name());
                    }
                } else {
                    for (EntityType type : EntityType.values()) {
                        if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) result.add(type.name());
                    }
                }
                return result;
            }
        };
        try {
            handler.addCommand(spawnMobCmd);
        } catch (CommandNotFoundException e) {
            e.printStackTrace();
        }
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
