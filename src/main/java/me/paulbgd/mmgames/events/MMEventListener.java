package me.paulbgd.mmgames.events;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.Settings.GameSettings;
import me.paulbgd.mmgames.game.GameType;
import me.paulbgd.mmgames.game.MMTeam;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.projectiles.events.CustomProjectileHitEvent;
import me.paulbgd.mmgames.projectiles.events.ItemProjectileHitEvent;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MMEventListener implements Listener {

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
         MMEventHandler.onLeftClick(event);
      } else {
         MMEventHandler.onRightClick(event);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onItemProjectileHit(ItemProjectileHitEvent event) {
      MMEventHandler.onItemHit(event);
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onCustomProjectileHit(CustomProjectileHitEvent event) {
      if (event.getProjectileType() == EntityType.PRIMED_TNT) {

      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerDeath(PlayerDeathEvent event) {
      MMPlayer player = MMGames.getPlugin().getPlayer(event.getEntity());
      // clear the public things
      event.setDeathMessage(null);
      event.setDroppedExp(0);
      event.setKeepLevel(false);
      event.getDrops().clear();

      if (player.inGame() && event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
         MMPlayer killer = MMGames.getPlugin().getPlayer(event.getEntity().getKiller());
         player.getGame().broadcast(String.format("[e][l]%s[r][e] was killed by [l]%s[r][e]!", player.getName(), killer.getName()));
         killer.addStat("kills");
         player.addStat("deaths");
         MMEventHandler.onKillPlayer(killer, player);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      MMEventHandler.onPlayerRespawn(MMGames.getPlugin().getPlayer(event.getPlayer()));
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onEntityDeath(EntityDeathEvent event) {
      event.setDroppedExp(0);
      event.getDrops().clear();

      if (!(event.getEntity() instanceof Player) && event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) { // make sure not killed by player,
                                                                                                                                                // handled above
         MMPlayer killer = MMGames.getPlugin().getPlayer(event.getEntity().getKiller());
         MMEventHandler.onKillEntity(killer, event.getEntity());
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         MMPlayer damager = MMGames.getPlugin().getPlayer((Player) event.getDamager());
         if (event.getEntity() instanceof Player) {
            MMPlayer damaged = MMGames.getPlugin().getPlayer((Player) event.getEntity());
            if (GameSettings.GAME_TYPE == GameType.TEAM) {
               for (MMTeam team : damager.getGame().getTeams()) {
                  if (team.getPlayers().contains(damager) && team.getPlayers().contains(damaged)) {
                     // same team
                     event.setCancelled(true);
                     return;
                  }
               }
            }
            MMEventHandler.onPlayerHitPlayer(damager, damaged, event.getDamage());
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerJoin(PlayerJoinEvent event) {
      MMEventHandler.onPlayerJoinServer(MMGames.getPlugin().getPlayer(event.getPlayer()));
   }

}
