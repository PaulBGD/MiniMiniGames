package me.paulbgd.mmgames.utils.bar;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.server.v1_7_R1.Entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Util {
   public static boolean newProtocol = false;
   public static String version;
   public static Class<?> fakeDragonClass = FakeDragon.class;
   
   static {
      String name = Bukkit.getServer().getClass().getPackage().getName();
      String mcVersion = name.substring(name.lastIndexOf('.') + 1);

      version = mcVersion + ".";
   }

   public static FakeDragon newDragon(String message, Location loc) {
      FakeDragon fakeDragon = null;

      try {
         fakeDragon = (FakeDragon) fakeDragonClass.getConstructor(String.class, Location.class).newInstance(message, loc);
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (SecurityException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      }

      return fakeDragon;
   }

   // Reflection Util
   public static void sendPacket(Player p, Object packet) {
      try {
         Object nmsPlayer = ((CraftPlayer) p).getHandle();
         Field con_field = nmsPlayer.getClass().getField("playerConnection");
         Object con = con_field.get(nmsPlayer);
         Method packet_method = getMethod(con.getClass(), "sendPacket");
         packet_method.invoke(con, packet);
      } catch (SecurityException e) {
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      }
   }

   public static Class<?> getCraftClass(String ClassName) {
      String className = "net.minecraft.server." + version + ClassName;
      Class<?> c = null;
      try {
         c = Class.forName(className);
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      return c;
   }

   public static Object getHandle(World world) {
      Object nms_entity = null;
      Method entity_getHandle = getMethod(world.getClass(), "getHandle");
      try {
         nms_entity = entity_getHandle.invoke(world);
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      }
      return nms_entity;
   }

   public static Object getHandle(Entity entity) {
      Object nms_entity = null;
      Method entity_getHandle = getMethod(entity.getClass(), "getHandle");
      try {
         nms_entity = entity_getHandle.invoke(entity);
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      }
      return nms_entity;
   }

   public static Field getField(Class<?> cl, String field_name) {
      try {
         Field field = cl.getDeclaredField(field_name);
         return field;
      } catch (SecurityException e) {
         e.printStackTrace();
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      }
      return null;
   }

   public static Method getMethod(Class<?> cl, String method, Class<?>[] args) {
      for (Method m : cl.getMethods()) {
         if (m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes())) {
            return m;
         }
      }
      return null;
   }

   public static Method getMethod(Class<?> cl, String method, Integer args) {
      for (Method m : cl.getMethods()) {
         if (m.getName().equals(method) && args.equals(new Integer(m.getParameterTypes().length))) {
            return m;
         }
      }
      return null;
   }

   public static Method getMethod(Class<?> cl, String method) {
      for (Method m : cl.getMethods()) {
         if (m.getName().equals(method)) {
            return m;
         }
      }
      return null;
   }

   public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
      boolean equal = true;

      if (l1.length != l2.length)
         return false;
      for (int i = 0; i < l1.length; i++) {
         if (l1[i] != l2[i]) {
            equal = false;
            break;
         }
      }

      return equal;
   }

}