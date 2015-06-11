package me.paulbgd.mmgames.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_7_R1.DataWatcher;
import net.minecraft.server.v1_7_R1.EntityHorse;
import net.minecraft.server.v1_7_R1.EntityWitherSkull;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Hologram {

   private static int id = 9000;
   private static final double distance = 0.25;
   private final List<String> lines = new ArrayList<String>();
   private List<Integer> ids = new ArrayList<Integer>();
   private boolean showing = false;

   public Hologram(String... lines) {
      this.lines.addAll(Arrays.asList(lines));
   }

   public void show(Location loc) {
      if (showing == true) {
         try {
            throw new Exception("Is already showing!");
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      Location first = loc.clone().add(0, (this.lines.size() / 2) * distance, 0);
      for (int i = 0; i < this.lines.size(); i++) {
         ids.addAll(Arrays.asList(id, id + 1));
         showLine(first.clone(), this.lines.get(i));
         first.subtract(0, distance, 0);
      }
      showing = true;
   }

   public void destroy() {
      if (showing == false) {
         try {
            throw new Exception("Isn't showing!");
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      int[] ints = new int[this.ids.size()];
      for (int i = 0; i < ints.length; i++) {
         ints[i] = ids.get(i);
      }
      PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ints);
      for (Player player : Bukkit.getOnlinePlayers()) {
         ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
      }
      showing = false;
   }

   private static void showLine(Location loc, String text) {
      EntityHorse horse = new EntityHorse(((CraftWorld) loc.getWorld()).getHandle());
      horse.setPosition(loc.getX(), loc.getY() + 55, loc.getZ());
      PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(horse);
      setIntField(packet, "a", id);
      DataWatcher watcher = new DataWatcher(horse);
      watcher.a(10, text);
      watcher.a(11, (byte) 1);
      watcher.a(12, -1700000);
      setIntField(packet, "l", watcher);
      for (Player player : loc.getWorld().getPlayers()) {
         ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
      }
      EntityWitherSkull skull = new EntityWitherSkull(((CraftWorld) loc.getWorld()).getHandle());
      PacketPlayOutSpawnEntity skullPacket = new PacketPlayOutSpawnEntity(skull, EntityType.WITHER_SKULL.getTypeId());
      setIntField(skullPacket, "a", id + 1);
      for (Player player : loc.getWorld().getPlayers()) {
         ((CraftPlayer) player).getHandle().playerConnection.sendPacket(skullPacket);
      }
      id += 2;
   }

   private static void setIntField(Object obj, String name, Object value) {
      try {
         Field field = obj.getClass().getDeclaredField(name);
         field.setAccessible(true);
         field.set(obj, value);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
