package me.paulbgd.mmgames.cmd;

import me.paulbgd.mmgames.game.MMGame;
import me.paulbgd.mmgames.player.MMPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdJoin extends BaseCommand {

   public CmdJoin() {
      super("join");
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String value, String[] args) {
      if(!(sender instanceof Player)) {
         sender.sendMessage("You are not a player!");
         return false;
      }
      MMPlayer player = plugin.getPlayer((Player) sender);
      if(player.inGame()) {
         player.sendMessage("[c]You are currently in a game!");
      } else {
         if(args.length == 1) {
            String gameName = args[0];
            if(plugin.doesGameExist(gameName)) {
               MMGame game = plugin.getGame(gameName);
               if(!game.isRunning()) {
                  game.addPlayer(player, true);
               } else {
                  player.sendMessage("[c]That game is running!");
               }
            } else {
               player.sendMessage("[c]That game does not exist!");
            }
         } else {
            player.sendMessage("[c]Proper usage: /join <game>");
         }
      }
      return true;
   }
   
}
