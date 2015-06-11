package me.paulbgd.mmgames.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.player.MMPlayer;

import org.bukkit.Bukkit;

public class Bungee {

   static {
      Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(MMGames.getPlugin(), "BungeeCord");
   }

   public static void disconnect(MMPlayer player) {
      player.sendMessage("[c]Connecting to lobby..");
      Bungee.send(player, "Connect", "lobby");
   }

   private static void send(MMPlayer player, String... stuff) {
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(b);

      try {
         for (String string : stuff) {
            out.writeUTF(string);
         }
      } catch (Exception e) {
         // impossibro
         e.printStackTrace();
      }
      player.getPlayer().sendPluginMessage(MMGames.getPlugin(), "BungeeCord", b.toByteArray());
   }
}
