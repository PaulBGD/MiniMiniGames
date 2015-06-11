package me.paulbgd.mmgames;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.paulbgd.mmgames.Settings.GameSettings;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.Utils;
import net.minecraft.server.v1_7_R1.EnumClientCommand;
import net.minecraft.server.v1_7_R1.PacketPlayInClientCommand;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

   private MMGames plugin = MMGames.getPlugin();

   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent event) {
      // set a quick format
      MMPlayer player = plugin.getPlayer(event.getPlayer());
      StringBuilder name = new StringBuilder();
      if (player.getRank().getLevel() != 0) {
         name.append(player.getRank().getColor() + "" + ChatColor.BOLD + player.getRank().name() + ChatColor.RESET + " ");
      }
      name.append(ChatColor.GRAY + "%s:" + ChatColor.RESET + " %s");
      event.setFormat(name.toString());
   }

   @EventHandler(priority = EventPriority.LOWEST)
   // this NEEDS to be called before the player is removed
   public void onPlayerQuit(PlayerQuitEvent event) {
      MMPlayer player = plugin.getPlayer(event.getPlayer());
      if (player.inGame()) {
         player.getGame().removePlayer(player, true);
      }
      MMGames.getPlugin().removePlayer(MMGames.getPlugin().getPlayer(event.getPlayer()));
   }

   @EventHandler
   public void onPlayerDeath(final PlayerDeathEvent event) {
      // auto-respawn
      new BukkitRunnable() {
         public void run() {
            ((CraftPlayer) event.getEntity()).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
         }
      }.runTaskLater(plugin, 1L);
      MMPlayer player = this.plugin.getPlayer(event.getEntity());
      if (player.inGame() && !GameSettings.RESPAWN_AFTER_DEATH) {
         player.getGame().removePlayer(player, true);
      }
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      MMPlayer player = plugin.getPlayer(event.getPlayer());
      if (player.inGame()) {
         event.setRespawnLocation(player.getGame().getSpawnPoint());
      }
   }

   // for bungee
   @EventHandler
   public void onSignChange(SignChangeEvent event) {
      if (!(event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
         return;
      }
      if (event.getLine(0).equalsIgnoreCase("Server")) {
         String server = event.getLine(1);

         event.setLine(0, ChatColor.GOLD + "" + ChatColor.BOLD + "[ Server ]");
         event.setLine(1, event.getLine(2));
         event.setLine(2, "");
         event.setLine(3, ChatColor.BOLD + "Click to join");

         plugin.getConfig().set("Servers." + Utils.getStringBlock(event.getBlock().getLocation()), server);
         plugin.saveConfig();
      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Block block = event.getClickedBlock();
         if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            Location loc = block.getLocation();
            String configLocation = "Servers." + Utils.getStringBlock(loc);
            if (plugin.getConfig().isSet(configLocation)) {
               ByteArrayOutputStream b = new ByteArrayOutputStream();
               DataOutputStream out = new DataOutputStream(b);
               try {
                  out.writeUTF("Connect");
                  out.writeUTF(plugin.getConfig().getString(configLocation));
               } catch (IOException e) {
                  e.printStackTrace();
                  return;
               }
               event.getPlayer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            }
         }
      }
   }

}
