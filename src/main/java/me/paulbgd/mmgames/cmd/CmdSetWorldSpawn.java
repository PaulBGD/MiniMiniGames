package me.paulbgd.mmgames.cmd;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.player.MMPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSetWorldSpawn extends BaseCommand {

   public CmdSetWorldSpawn() {
      super("setworldspawn");
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
      Location l = player.getPlayer().getLocation();
      player.getPlayer().getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
      player.sendMessage("[a]Set spawn location!");
      return true;
   }

}
