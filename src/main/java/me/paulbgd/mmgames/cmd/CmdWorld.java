package me.paulbgd.mmgames.cmd;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.player.MMPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdWorld extends BaseCommand {

   public CmdWorld() {
      super("world");
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("You are not a player!");
         return true;
      }
      if (!sender.isOp()) {
         sender.sendMessage(ChatColor.RED + "You do not have permission!");
         return true;
      }
      MMPlayer player = MMGames.getPlugin().getPlayer(sender.getName());
      if (args.length != 1) {
         player.sendMessage("[c]Usage: /world <world>");
         StringBuilder worlds = new StringBuilder();
         for (World world : Bukkit.getWorlds()) {
            worlds.append(", ").append(world.getName());
         }
         player.sendMessage(String.format("[c]World List: %s", worlds.toString().replaceFirst(", ", "")));
         return true;
      }
      World world = Bukkit.getWorld(args[0]);
      if (world == null) {
         player.sendMessage("[c]That world does not exist!");
         return true;
      }
      player.getPlayer().teleport(world.getSpawnLocation());
      return true;
   }
}
