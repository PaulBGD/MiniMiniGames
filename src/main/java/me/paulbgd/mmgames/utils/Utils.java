package me.paulbgd.mmgames.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.player.MMPlayer;
import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.PacketPlayOutExplosion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public class Utils {
   
   public static void changeHealth(MMPlayer player, double health) {
      if (health <= 0) {
         removeHealth(player, health);
      } else {
         addHealth(player, health);
      }
   }

   public static void addHealth(MMPlayer player, double health) {
      Player bPlayer = player.getPlayer();
      if (bPlayer.getHealth() + health > bPlayer.getMaxHealth()) {
         health = bPlayer.getMaxHealth() - bPlayer.getHealth();
      }
      bPlayer.setHealth(bPlayer.getHealth() + health);
   }

   public static void removeHealth(MMPlayer player, double health) {
      Player bPlayer = player.getPlayer();
      if (bPlayer.getHealth() - health <= 0) {
         health = bPlayer.getHealth();
      }
      bPlayer.setHealth(bPlayer.getHealth() - health);
   }

   public static int getHardNextInt(int range) {
      int finalInt = 0;
      for (int i = 0; i < range; i++) {
         if (MMGames.random.nextBoolean()) {
            finalInt++;
         }
      }
      return finalInt;
   }

   public static List<Player> getNearbyPlayers(Location loc, double distance) {
      List<Player> players = new ArrayList<Player>();
      for (Player player : loc.getWorld().getPlayers()) {
         if (player.getLocation().distanceSquared(loc) <= distance * distance) {
            players.add(player);
         }
      }
      return players;
   }

   public static String formatSeconds(int seconds) {
      int millis = seconds * 1000;
      long min = TimeUnit.MILLISECONDS.toMinutes(millis);
      long sec = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
      if ((int) sec == 0 && min > 0) {
         if ((int) min == 1) {
            return min + " minute";
         } else {
            return min + " minutes";
         }
      } else if ((int) min == 0) {
         if ((int) sec == 1) {
            return sec + " second";
         } else {
            return sec + " seconds";
         }
      }
      return String.format("%d minute(s) and %d second(s)", min, sec);
   }

   public static String formatEnum(Enum<?> e) {
      String name = e.name().toLowerCase().replaceAll("_", " ");
      String[] arr = name.split(" ");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < arr.length; i++) {
         sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
      }
      return sb.toString().trim();
   }

   public static void sendExplosionPacket(Location loc, float size) {
      for (final Player player : loc.getWorld().getPlayers()) {
         if (player.getLocation().distance(loc) < 256) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutExplosion(loc.getX(), loc.getY(), loc.getZ(), size, new ArrayList<Block>(), null));
         }
      }
   }

   public static String color(String message) {
      for (ChatColor color : ChatColor.values()) {
         message = message.replaceAll("\\[" + color.getChar() + "\\]", color.toString());
      }
      return message;
   }

   public static Location getLocation(String string) {
      String[] split = string.split(";");

      String world = split[0];
      double x = Double.parseDouble(split[1]);
      double y = Double.parseDouble(split[2]);
      double z = Double.parseDouble(split[3]);
      float yaw = 0;
      float pitch = 0;

      if (split.length > 4) {
         yaw = Float.parseFloat(split[4]);
         pitch = Float.parseFloat(split[5]);
      }

      return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
   }

   public static String getString(Location location) {
      StringBuilder builder = new StringBuilder();
      builder.append(location.getWorld().getName()).append(";");
      builder.append(location.getX()).append(";");
      builder.append(location.getY()).append(";");
      builder.append(location.getZ()).append(";");
      builder.append(location.getYaw()).append(";");
      builder.append(location.getPitch());

      return builder.toString();
   }

   public static String getStringBlock(Location location) {
      StringBuilder builder = new StringBuilder();
      builder.append(location.getWorld().getName()).append(";");
      builder.append(location.getBlockX()).append(";");
      builder.append(location.getBlockY()).append(";");
      builder.append(location.getBlockZ()).append(";");

      return builder.toString();
   }

   private static MMGames plugin = MMGames.getPlugin();

   public static void registerCommand(String... aliases) {
      PluginCommand command = getCommand(aliases[0], plugin);

      command.setAliases(Arrays.asList(aliases));
      getCommandMap().register(plugin.getDescription().getName(), command);
   }

   private static PluginCommand getCommand(String name, MMGames plugin) {
      PluginCommand command = null;

      try {
         Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
         c.setAccessible(true);

         command = c.newInstance(name, plugin);
      } catch (SecurityException e) {
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      }

      return command;
   }

   private static CommandMap getCommandMap() {
      CommandMap commandMap = null;

      try {
         if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);

            commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
         }
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (SecurityException e) {
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }

      return commandMap;
   }
}
