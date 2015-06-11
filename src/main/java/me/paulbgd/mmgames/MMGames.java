package me.paulbgd.mmgames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import me.paulbgd.mmgames.Settings.WorldSettings;
import me.paulbgd.mmgames.cmd.BaseCommand;
import me.paulbgd.mmgames.cmd.CmdAddDrop;
import me.paulbgd.mmgames.cmd.CmdCreate;
import me.paulbgd.mmgames.cmd.CmdJoin;
import me.paulbgd.mmgames.cmd.CmdSetWorldSpawn;
import me.paulbgd.mmgames.cmd.CmdWorld;
import me.paulbgd.mmgames.events.MMEventHandler;
import me.paulbgd.mmgames.events.MMEventListener;
import me.paulbgd.mmgames.game.MMGame;
import me.paulbgd.mmgames.items.ItemListener;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.MMSQL;
import me.paulbgd.mmgames.utils.Utils;
import me.paulbgd.mmgames.utils.bar.BarAPI;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public abstract class MMGames extends BarAPI {

   public static Random random = new Random();
   private static MMGames plugin;
   protected MMSQL sql;

   private HashMap<String, MMPlayer> players = new HashMap<String, MMPlayer>();
   private HashMap<String, MMGame> games = new HashMap<String, MMGame>();

   public abstract void onPluginEnable();

   @Override
   public void onEnable() {
      try {
         FileInputStream in = new FileInputStream("server.properties");
         Properties props = new Properties();
         props.load(in);
         in.close();
         if (props.getProperty("announce-player-achievements").equalsIgnoreCase("true")) {
            FileOutputStream out = new FileOutputStream("server.properties");
            props.setProperty("announce-player-achievements", "false");
            props.store(out, null);
            out.close();

            this.getServer().shutdown();
            return;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      super.onEnable();

      long time = new Date().getTime();
      MMGames.plugin = this;

      if (!this.getConfig().isSet("db_url")) {
         this.getConfig().set("db_url", "jdbc:mysql://[host][,failoverhost...][:port]");
      }
      if (!this.getConfig().isSet("db_user")) {
         this.getConfig().set("db_user", "exampleuser");
      }
      if (!this.getConfig().isSet("db_pass")) {
         this.getConfig().set("db_pass", "abcdeSomePassword");
      }

      this.saveConfig();

      this.sql = new MMSQL();

      this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
      this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
      this.getServer().getPluginManager().registerEvents(new MMEventListener(), this);

      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

      File gameFolder = new File(this.getDataFolder(), "games");
      if (gameFolder.exists()) {
         for (File configFile : gameFolder.listFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            MMGame game = new MMGame(configFile, config);
            this.games.put(game.getName().toLowerCase(), game);
         }
      } else {
         // create if it doesn't exist
         gameFolder.mkdirs();
      }

      if (!this.getConfig().isSet("Spawn_Location")) {
         this.getConfig().set("Spawn_Location", Utils.getString(Bukkit.getWorlds().get(0).getSpawnLocation()));
      }

      this.onPluginEnable();

      if (Settings.COMMAND_JOIN)
         this.registerCommand(new CmdJoin());
      this.registerCommand(new CmdAddDrop());
      this.registerCommand(new CmdCreate());
      this.registerCommand(new CmdSetWorldSpawn());
      this.registerCommand(new CmdWorld());

      MMEventHandler.registerListener(new ItemListener());

      // load after plugin is enabled
      for (Player player : Bukkit.getOnlinePlayers()) { // in the case of a reload
         PlayerJoinEvent event = new PlayerJoinEvent(player, "joined");
         Bukkit.getPluginManager().callEvent(event);
      }

      for (World world : Bukkit.getWorlds()) {
         world.setAutoSave(false);
         world.setSpawnFlags(false, false);
         world.setStorm(false);
         world.setThundering(false);
         world.setTime(500);
         world.setWeatherDuration(Integer.MAX_VALUE);
         world.setGameRuleValue("commandBlockOutput", "false");
         world.setGameRuleValue("doDaylightCycle", Boolean.toString(WorldSettings.TIME_CHANGE));
         world.setGameRuleValue("doFireTick", "false");
         world.setGameRuleValue("doMobLoot", "false");
         world.setGameRuleValue("doMobSpawning", "false");
         world.setGameRuleValue("doTileDrops", "false");
         world.setGameRuleValue("keepInventory", "false");
         world.setGameRuleValue("mobGriefing", "false");
         this.clearMobs(world);
      }

      long ms = new Date().getTime() - time;
      this.getLogger().info(this.getDescription().getName() + " enabled in " + ms + "ms.");
   }

   @Override
   public void onDisable() {
      for (World world : Bukkit.getWorlds()) {
         this.clearMobs(world);
      }
   }

   public void tick(MMPlayer player, MMGame game) {
   }

   public MMPlayer loadPlayer(MMPlayer player) {
      return player;
   }

   public void registerCommand(BaseCommand command) {
      Validate.notNull(command);
      Validate.notNull(command.getNames());
      Validate.notNull(command.getNames()[0]);
      Utils.registerCommand(command.getNames());
      getCommand(command.getNames()[0]).setExecutor(command);
   }

   public MMGame getGame(String name) {
      return this.games.get(name.toLowerCase());
   }

   public boolean doesGameExist(String name) {
      return this.games.containsKey(name.toLowerCase());
   }

   public Collection<MMGame> getAllGames() {
      return this.games.values();
   }

   public void addGame(MMGame game) {
      String name = game.getName().toLowerCase();
      if (this.games.containsKey(name)) {
         this.games.remove(name);
      }
      this.games.put(name, game);
   }

   private void clearMobs(World world) {
      for (Entity entity : world.getEntities()) {
         if (!(entity instanceof Hanging) && !(entity instanceof Player)) {
            entity.remove();
         }
      }
   }

   public MMPlayer getPlayer(String player) {
      return this.players.get(player.toLowerCase());
   }

   public MMPlayer getPlayer(Player player) {
      return this.getPlayer(player.getName());
   }

   public boolean hasPlayer(MMPlayer player) {
      return this.hasPlayer(player.getName());
   }

   public boolean hasPlayer(String name) {
      return this.players.containsKey(name.toLowerCase());
   }

   // should be overwritten
   public Location getSpawn() {
      if (this.getConfig().isSet("Spawn_Location")) {
         return Utils.getLocation(this.getConfig().getString("Spawn_Location"));
      } else {
         return Bukkit.getWorlds().get(0).getSpawnLocation();
      }
   }

   public void addPlayer(MMPlayer player) {
      String name = player.getName().toLowerCase();
      if (players.containsKey(name)) {
         players.remove(name);
      }
      players.put(name, player);
   }

   public void removePlayer(MMPlayer player) {
      String name = player.getName().toLowerCase();
      if (players.containsKey(name)) {
         players.remove(name);
      }
   }

   public MMSQL getSQL() {
      return this.sql;
   }

   public static MMGames getPlugin() {
      return MMGames.plugin;
   }

}
