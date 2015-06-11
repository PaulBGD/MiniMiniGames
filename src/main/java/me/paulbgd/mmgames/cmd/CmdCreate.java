package me.paulbgd.mmgames.cmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.game.MMGame;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CmdCreate extends BaseCommand {

   private HashMap<MMPlayer, FileConfiguration> setting = new HashMap<MMPlayer, FileConfiguration>();

   public CmdCreate() {
      super("creation", "create");
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String value, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("You are not a player!");
         return true;
      }
      MMPlayer player = plugin.getPlayer(sender.getName());
      if (!sender.isOp()) {
         player.sendMessage("[c]You do not have permission to use that command!");
         return true;
      }
      if (args.length >= 3 && args[0].equalsIgnoreCase("config")) {
         if (MMGames.getPlugin().doesGameExist(args[1])) {
            MMGame game = MMGames.getPlugin().getGame(args[1]);
            FileConfiguration config = game.getConfig();
            if (args.length >= 4) {
               config.set(args[2], args[3]);
               player.sendMessage(String.format("[e]Set %s to %s", args[2], args[3]));
            } else {
               config.set(args[2], null);
               player.sendMessage(String.format("[e]Set %s to null", args[2]));
            }
         } else {
            player.sendMessage("[c]No such game!");
         }
         return true;
      }
      if (args.length < 1) {
         player.sendMessage("[c]Too little arguments!");
         return true;
      }
      switch (args[0].toLowerCase()) {
      case "new":
         if (args.length < 2) {
            player.sendMessage("[c]Too little arguments!");
            return true;
         }
         String gameName = args[1];
         if (plugin.doesGameExist(gameName)) {
            player.sendMessage("[c]That game already exists!");
            return true;
         }
         if (setting.containsKey(player)) {
            setting.remove(player);
         }
         File file = new File(plugin.getDataFolder(), "games" + File.separator + gameName + ".yml");
         try {
            file.createNewFile();
         } catch (IOException e) {
            player.sendMessage("[c]Failed to make file! Path: " + file.getAbsolutePath());
            e.printStackTrace();
            return true;
         }
         FileConfiguration config = YamlConfiguration.loadConfiguration(file);
         config.set("Name", args[1]);
         config.set("Lobby", Utils.getString(player.getPlayer().getLocation()));
         player.sendMessage("[a]Created game object");
         setting.put(player, config);
         return true;
      case "addspawn":
         if (!setting.containsKey(player)) {
            player.sendMessage("You are not making a game!");
            return true;
         }
         config = setting.get(player);
         List<Object> spawns = new ArrayList<Object>();
         if (config.isSet("Spawnpoints")) {
            spawns.addAll(config.getList("Spawnpoints"));
         }
         spawns.add(Utils.getString(player.getPlayer().getLocation()));
         config.set("Spawnpoints", spawns);
         player.sendMessage("[a]Added spawnpoint");
         return true;
      case "setlobby":
         if (!setting.containsKey(player)) {
            player.sendMessage("You are not making a game!");
            return true;
         }
         config = setting.get(player);
         config.set("Lobby", Utils.getString(player.getPlayer().getLocation()));
         player.sendMessage("[a]Set lobby!");
         return true;
      case "save":
         if (!setting.containsKey(player)) {
            player.sendMessage("You are not making a game!");
            return true;
         }
         config = setting.get(player);
         file = new File(plugin.getDataFolder(), "games" + File.separator + config.getString("Name") + ".yml");
         try {
            config.save(file);
         } catch (IOException e) {
            player.sendMessage("[c]Failed to save!");
            e.printStackTrace();
         }
         setting.remove(player);
         plugin.addGame(new MMGame(file, config));
         player.sendMessage("[a]Saved!");
         return true;
      }
      player.sendMessage("[c]Invalid argument!");
      return true;
   }

}
