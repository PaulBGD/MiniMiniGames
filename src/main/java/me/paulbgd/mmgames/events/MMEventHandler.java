package me.paulbgd.mmgames.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.events.MMListener.ListenerType;
import me.paulbgd.mmgames.game.MMGame;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.projectiles.CustomProjectile;
import me.paulbgd.mmgames.projectiles.TNTProjectile;
import me.paulbgd.mmgames.projectiles.events.CustomProjectileHitEvent;
import me.paulbgd.mmgames.projectiles.events.ItemProjectileHitEvent;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MMEventHandler {

   private static HashMap<ListenerType, List<MMListener>> listeners = new HashMap<ListenerType, List<MMListener>>();

   static {
      for (ListenerType type : ListenerType.values()) {
         listeners.put(type, new ArrayList<MMListener>());
      }
   }

   public static void registerListener(MMListener listener) {
      listeners.get(listener.getType()).add(listener);
   }

   public static void onRightClick(PlayerInteractEvent event) {
      MMPlayer player = MMGames.getPlugin().getPlayer(event.getPlayer());
      Validate.notNull(player);
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isSpectating()) {
         event.setCancelled(true);
      }
      for (MMListener listener : listeners.get(ListenerType.RIGHT_CLICK)) {
         if (listener.onEvent(new Object[] { player, event.getAction() })) {
            break;
         }
      }
   }

   public static void onLeftClick(PlayerInteractEvent event) {
      MMPlayer player = MMGames.getPlugin().getPlayer(event.getPlayer());
      for (MMListener listener : listeners.get(ListenerType.LEFT_CLICK)) {
         if (listener.onEvent(new Object[] { player, event.getAction() })) {
            break;
         }
      }
   }

   public static void onItemHit(ItemProjectileHitEvent event) {
      LivingEntity shooter = event.getProjectile().getShooter();
      MMPlayer player = null;

      if (shooter instanceof Player) {
         player = MMGames.getPlugin().getPlayer((Player) shooter);
      }
      CustomProjectile proj = event.getProjectile();
      ItemStack item = event.getItemStack();
      Block hitBlock = event.getHitBlock();
      LivingEntity hitEntity = event.getHitEntity();

      for (MMListener listener : listeners.get(ListenerType.ITEM_HIT)) {
         if (listener.onEvent(new Object[] { player, proj, item, hitBlock, hitEntity })) {
            break;
         }
      }
   }

   public static void onTNTHit(CustomProjectileHitEvent event) {
      TNTProjectile proj = (TNTProjectile) event.getProjectile();
      for (MMListener listener : listeners.get(ListenerType.TNT_HIT)) {
         if (listener.onEvent(new Object[] { proj })) {
            break;
         }
      }
   }

   public static void onJoinGame(MMPlayer player, MMGame game) {
      for (MMListener listener : listeners.get(ListenerType.JOIN_GAME)) {
         if (listener.onEvent(new Object[] { player, game })) {
            break;
         }
      }
   }

   public static void onLeaveGame(MMPlayer player, MMGame game) {
      for (MMListener listener : listeners.get(ListenerType.LEAVE_GAME)) {
         if (listener.onEvent(new Object[] { player, game })) {
            break;
         }
      }
   }

   public static void onKillPlayer(MMPlayer killer, MMPlayer killed) {
      for (MMListener listener : listeners.get(ListenerType.KILL_PLAYER)) {
         if (listener.onEvent(new Object[] { killer, killed })) {
            break;
         }
      }
   }

   public static void onKillEntity(MMPlayer killer, LivingEntity killed) {
      for (MMListener listener : listeners.get(ListenerType.KILL_ENTITY)) {
         if (listener.onEvent(new Object[] { killer, killed })) {
            break;
         }
      }
   }

   public static void onPlayerRespawn(final MMPlayer player) {
      new BukkitRunnable() {
         @Override
         public void run() {
            for (MMListener listener : listeners.get(ListenerType.PLAYER_RESPAWN)) {
               if (listener.onEvent(new Object[] { player })) {
                  break;
               }
            }
         }
      }.runTaskLater(MMGames.getPlugin(), 2);
   }

   public static void onGameStart(MMGame game) {
      for (MMListener listener : listeners.get(ListenerType.GAME_START)) {
         if (listener.onEvent(new Object[] { game })) {
            break;
         }
      }
   }

   public static void onGameEnd(MMGame game, MMPlayer winner) {
      Object[] object;
      if (winner == null) {
         object = new Object[] { game };
      } else {
         object = new Object[] { game, winner };
      }
      for (MMListener listener : listeners.get(ListenerType.GAME_END)) {
         if (listener.onEvent(object)) {
            break;
         }
      }
   }

   public static void onStartSpectating(MMPlayer player) {
      for (MMListener listener : listeners.get(ListenerType.START_SPECTATING)) {
         if (listener.onEvent(new Object[] { player })) {
            break;
         }
      }
   }

   public static void onPlayerHitPlayer(MMPlayer damager, MMPlayer damaged, double damage) {
      for (MMListener listener : listeners.get(ListenerType.HIT_PLAYER)) {
         if (listener.onEvent(new Object[] { damager, damaged, damage })) {
            break;
         }
      }
   }

   public static void onPlayerJoinServer(MMPlayer player) {
      for (MMListener listener : listeners.get(ListenerType.JOIN_SERVER)) {
         if (listener.onEvent(new Object[] { player })) {
            break;
         }
      }
   }

}
