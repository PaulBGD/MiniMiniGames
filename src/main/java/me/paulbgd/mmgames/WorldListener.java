package me.paulbgd.mmgames;

import me.paulbgd.mmgames.Settings.GameSettings;
import me.paulbgd.mmgames.player.MMPlayer;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class WorldListener implements Listener {

   @EventHandler
   public void onPlayerDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         MMPlayer player = MMGames.getPlugin().getPlayer((Player) event.getEntity());
         if (!(player.inGame() && player.getGame().isRunning()) || (player.inGame() && player.isSpectating())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent event) {
      event.blockList().clear();
   }

   @EventHandler
   public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
      event.setCancelled(true);
      event.getPlayer().updateInventory();
   }

   @EventHandler
   public void onPlayerBucketFill(PlayerBucketFillEvent event) {
      event.setCancelled(true);
      event.getPlayer().updateInventory();
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent event) {
      event.setCancelled(!(event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE));
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      event.setCancelled(!(event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE));
      if (event.isCancelled()) {
         event.getPlayer().updateInventory();
      }
   }

   @EventHandler
   public void onHangingBreak(HangingBreakEvent event) {
      if (event.getCause() == RemoveCause.ENTITY) {
         HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) event;
         if (entityEvent.getRemover() instanceof Player) {
            Player remover = (Player) entityEvent.getRemover();
            if (remover.isOp() && remover.getGameMode() == GameMode.CREATIVE) {
               return;
            }
         }
      }
      event.setCancelled(true);
   }

   @EventHandler
   public void onEntityCombustEvent(EntityCombustEvent event) {
      if (event.getDuration() == 8 && !(event.getEntity() instanceof Player)) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onBlockFade(BlockFadeEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onLeavesDecay(LeavesDecayEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onPlayerDropItem(PlayerDropItemEvent event) {
      event.setCancelled(true);
   }

   @EventHandler
   public void onBlockIgnite(BlockIgniteEvent event) {
      if (event.getCause() == IgniteCause.LIGHTNING) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      event.setJoinMessage(null);
      MMGames.getPlugin().addPlayer(MMGames.getPlugin().getSQL().loadPlayer(event.getPlayer()));
      event.getPlayer().teleport(MMGames.getPlugin().getSpawn());
      for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
         event.getPlayer().removePotionEffect(effect.getType());
      }
      event.getPlayer().getInventory().clear();
   }

   @EventHandler(priority = EventPriority.MONITOR)
   // needs to be called last
   public void onPlayerQuit(PlayerQuitEvent event) {
      event.setQuitMessage(null);
      for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
         event.getPlayer().removePotionEffect(effect.getType());
      }
      event.getPlayer().getInventory().clear();
   }

   @EventHandler
   public void onPlayerKick(PlayerKickEvent event) {
      event.setLeaveMessage(null);
   }

   @EventHandler
   public void onHungerChange(FoodLevelChangeEvent event) {
      MMPlayer player = MMGames.getPlugin().getPlayer(event.getEntity().getName());
      if (!(player.inGame() && GameSettings.HUNGER)) {
         event.setFoodLevel(20);
      }
   }

}
