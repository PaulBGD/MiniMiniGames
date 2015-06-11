package me.paulbgd.mmgames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public abstract class PaloozaPlugin extends MMGames implements Listener {

   @Override
   public void onEnable() {
      super.onEnable();

      Bukkit.getPluginManager().registerEvents(this, this);
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
      String ip = event.getAddress().getHostAddress();
      if (!ip.equals("127.0.0.1")) {
         event.disallow(Result.KICK_OTHER, ChatColor.RED + "Eh?");
      }
   }

}
