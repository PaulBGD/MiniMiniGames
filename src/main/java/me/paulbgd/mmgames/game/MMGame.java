package me.paulbgd.mmgames.game;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.Settings.GameSettings;
import me.paulbgd.mmgames.drops.Drops;
import me.paulbgd.mmgames.events.MMEventHandler;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.MMData;
import me.paulbgd.mmgames.utils.Utils;
import net.minecraft.util.com.google.common.math.IntMath;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

public class MMGame extends MMData {

   private final File file;
   private final FileConfiguration config;
   private final String name;

   private final List<MMTeam> teams = new ArrayList<MMTeam>();
   private final List<MMPlayer> players = new ArrayList<MMPlayer>();
   private final List<Location> spawnpoints = new ArrayList<Location>();
   private final List<Location> drops = new ArrayList<Location>();
   private final List<BukkitRunnable> tickers = new ArrayList<BukkitRunnable>();
   private final List<BlockState> destroyables = new ArrayList<BlockState>();
   private final Location lobby;
   private int last = 0;
   private int playersNeeded;
   private int maxPlayers;
   private boolean running = false;
   private int timer = GameSettings.TIMER;

   private BukkitRunnable countdown;

   public MMGame(File file, FileConfiguration config) {
      this.file = file;
      this.config = config;

      this.name = this.config.getString("Name", "Default Name");

      for (Object obj : this.config.getList("Spawnpoints")) {
         this.spawnpoints.add(Utils.getLocation(obj.toString()));
      }
      if (this.getConfig().isSet("Drops")) {
         for (Object obj : this.config.getList("Drops")) {
            this.drops.add(Utils.getLocation(obj.toString()));
         }
      }
      this.lobby = Utils.getLocation(this.config.getString("Lobby"));
      this.playersNeeded = this.config.getInt("Players-Needed", 2);
      this.maxPlayers = this.config.getInt("Max-Players", 16);
   }

   public void setMaxPlayers(int maxPlayers) {
      this.maxPlayers = maxPlayers;
   }

   public void addDestroyable(BlockState old) {
      this.destroyables.add(old);
   }

   public boolean isRunning() {
      return this.running;
   }

   public List<MMPlayer> getPlayers() {
      return this.players;
   }

   public List<MMTeam> getTeams() {
      return this.teams;
   }

   public MMPlayer getRandomPlayer() {
      return this.players.get(MMGames.random.nextInt(this.players.size()));
   }

   public void addTimer(BukkitRunnable runnable, long wait, long timer) {
      tickers.add(runnable);
      runnable.runTaskTimer(MMGames.getPlugin(), wait, timer);
   }

   public void removeTimer(BukkitRunnable runnable) {
      this.tickers.remove(runnable);
   }

   public void stop(MMPlayer winner) {
      MMEventHandler.onGameEnd(this, winner);
      for (MMPlayer player : new ArrayList<MMPlayer>(this.players)) {
         this.removePlayer(player, false);
      }
      Bukkit.broadcastMessage(ChatColor.YELLOW + winner.getName() + " won on " + this.name);
      this.stop();
   }

   public void stop(MMTeam team) {
      MMEventHandler.onGameEnd(this, team.getPlayers().get(0));
      for (MMPlayer player : new ArrayList<MMPlayer>(this.players)) {
         this.removePlayer(player, false);
      }
      Bukkit.broadcastMessage(ChatColor.YELLOW + team.getName() + " won on " + this.name);
      this.stop();
   }

   private void stop() {
      for (BukkitRunnable runnable : tickers) {
         runnable.cancel();
      }
      while (!this.destroyables.isEmpty()) {
         BlockState state = this.destroyables.get(0);
         state.setType(Material.AIR);
         this.destroyables.remove(0);
      }
      this.running = false;
      this.clearData();
   }

   private void recalculatePlayers() {
      if (running) {
         int alive = 0;
         if (GameSettings.GAME_TYPE == GameType.PLAYER) {
            for (MMPlayer player : this.players) {
               if (!player.isSpectating()) {
                  alive++;
               }
            }
         } else {

         }
         if (alive <= 1) { // last person standing?
            for (MMPlayer player : new ArrayList<MMPlayer>(this.players)) {
               if (player.isSpectating()) {
                  continue;
               }
               MMEventHandler.onGameEnd(this, player);
               this.removePlayer(player, false);
               Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " won on " + this.name);
            }
            for (BukkitRunnable runnable : tickers) {
               runnable.cancel();
            }
            while (!this.destroyables.isEmpty()) {
               BlockState state = this.destroyables.get(0);
               state.setType(Material.AIR);
               state.update(false); // update without physics
               this.destroyables.remove(0);
            }
            this.running = false;
            this.clearData();
         }
      } else {
         if (this.players.size() >= this.playersNeeded && this.countdown == null) {
            timer = GameSettings.TIMER;
            this.countdown = new BukkitRunnable() {

               @Override
               public void run() {
                  switch (timer) {
                  case 30:
                  case 25:
                  case 20:
                  case 15:
                  case 10:
                  case 5:
                  case 4:
                  case 3:
                  case 2:
                     broadcast("[e]Game starting in " + timer + " seconds!");
                     break;
                  case 1:
                     broadcast("[e]Game starting in " + timer + " second!");
                     break;
                  case 0:
                     if (players.size() < playersNeeded) {
                        timer += 10;
                        broadcast("[c]Not enough players! 10 seconds added to timer.");
                     } else {
                        broadcast("[e]Game starting!");
                        this.cancel();
                        MMGame.this.countdown = null;
                        MMGame.this.start();
                     }
                     break;
                  }
                  timer--;
               }
            };
            this.countdown.runTaskTimer(MMGames.getPlugin(), 0, 20);
         }
      }
   }

   public void start() {
      this.running = true;
      MMEventHandler.onGameStart(this);
      Collections.shuffle(this.players);
      boolean teleport = true;
      if (GameSettings.GAME_TYPE == GameType.TEAM) {
         this.teams.clear();
         teleport = false;
         int partitionSize = IntMath.divide(this.players.size(), 2, RoundingMode.UP);
         List<List<MMPlayer>> partitions = Lists.partition(this.players, partitionSize);
         for (int i = 0; i < 2; i++) {
            this.teams.add(new MMTeam(Integer.toString(i)).addPlayers(partitions.get(i)));
         }
         for (MMTeam team : this.teams) {
            Location spawnpoint = this.getSpawnPoint();
            for (MMPlayer player : team.getPlayers()) {
               player.getPlayer().teleport(spawnpoint);
            }
         }
      }
      for (MMPlayer player : this.players) {
         if (!GameSettings.START_IN_SPAWNPOINTS && teleport) {
            player.getPlayer().teleport(this.getSpawnPoint());
         }
         if (player.hasKit()) {
            player.getKit().onSpawn(player);
            player.sendMessage(String.format("[a][l]You received your %s kit!", player.getKit().getName()));
         }
         MMEventHandler.onJoinGame(player, this);
      }
      if (GameSettings.DROP_TIMER != 0) {
         this.addTimer(new BukkitRunnable() {
            @Override
            public void run() {
               Drops.drop(MMGame.this);
            }
         }, 5 * 20, GameSettings.DROP_TIMER);
      }
      this.addTimer(new BukkitRunnable() {
         @Override
         public void run() {
            for (MMPlayer player : MMGame.this.getPlayers()) {
               if (player.isSpectating()) {
                  continue;
               }
               MMGames.getPlugin().tick(player, MMGame.this);
            }
         }
      }, 1, 1);
   }

   public void broadcast(String message) {
      for (MMPlayer player : this.players) {
         player.sendMessage(message);
      }
   }

   public void broadcast(String message, Object... args) {
      for (MMPlayer player : this.players) {
         player.sendMessage(message, args);
      }
   }

   public Location getSpawnPoint() {
      if (++last == spawnpoints.size()) {
         last = 0;
      }
      return spawnpoints.get(last);
   }

   public List<Location> getLocations() {
      return this.spawnpoints;
   }

   public List<Location> getDropLocations() {
      return this.drops;
   }

   public Location getLobby() {
      return this.lobby;
   }

   public void addPlayer(final MMPlayer player, boolean recalculate) {
      if (this.players.size() > this.maxPlayers) {
         player.sendMessage("[c]This game is full!");
         return;
      }
      if (!players.contains(player)) {
         players.add(player);
      }
      player.reset();
      player.setGame(this);

      if (GameSettings.START_IN_SPAWNPOINTS) {
         final Location loc = this.getSpawnPoint();
         Validate.notNull(loc);
         player.getPlayer().teleport(loc);
         new BukkitRunnable() {
            @Override
            public void run() {
               if (MMGame.this.isRunning()) {
                  this.cancel();
                  return;
               }
               player.getPlayer().teleport(loc);
            }
         }.runTaskTimer(MMGames.getPlugin(), 0, 2);
      } else {
         player.getPlayer().teleport(lobby);
      }

      if (GameSettings.PLAY_JOIN_FIREWORK) {
         Location loc = player.getPlayer().getLocation();
         Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
         FireworkMeta meta = fw.getFireworkMeta();
         meta.setPower(1);
         meta.addEffect(FireworkEffect.builder().withColor(Color.RED, Color.WHITE).withFlicker().withTrail().withFade(Color.RED).build());
         fw.setFireworkMeta(meta);
      }

      if (recalculate) {
         this.recalculatePlayers();
      }
   }

   public void removePlayer(MMPlayer player, boolean recalculate) {
      if (!player.isSpectating()) {
         if (player.hasData("kit")) {
            player.removeData("kit");
         }
         MMEventHandler.onLeaveGame(player, this);
         player.getPlayer().setMaxHealth(20);
         player.getPlayer().setHealth(20);

         if (player.getPlayer().isOnline()) {
            player.getPlayer().teleport(MMGames.getPlugin().getSpawn());
            player.reset();
            for (Player other : Bukkit.getOnlinePlayers()) {
               if (!other.canSee(player.getPlayer())) {
                  other.showPlayer(player.getPlayer());
               }
            }
            if (players.contains(player)) {
               players.remove(player);
            }
            player.setGame(null);
         }
      } else {
         player.reset();
         for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.canSee(player.getPlayer())) {
               other.showPlayer(player.getPlayer());
            }
         }
         if (players.contains(player)) {
            players.remove(player);
         }
         player.setGame(null);
      }
      if (recalculate) {
         this.recalculatePlayers();
      }
   }

   public String getName() {
      return this.name;
   }

   public FileConfiguration getConfig() {
      return this.config;
   }

   public void saveConfig() {
      try {
         this.config.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
