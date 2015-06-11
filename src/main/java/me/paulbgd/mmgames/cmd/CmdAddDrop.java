package me.paulbgd.mmgames.cmd;

import java.util.ArrayList;
import java.util.List;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.game.MMGame;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAddDrop extends BaseCommand {

   public CmdAddDrop() {
      super("adddrop");
   }

   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("You are not a player!");
         return true;
      }
      MMPlayer player = plugin.getPlayer(sender.getName());
      if (!sender.isOp()) {
         player.sendMessage("[c]You do not have permission to use that command!");
         return true;
      }
      if (args.length != 1) {
         player.sendMessage("[c]Usage: /adddrop <game>");
         return true;
      }
      if (!MMGames.getPlugin().doesGameExist(args[0])) {
         player.sendMessage("[c]Invalid game!");
         return true;
      }
      MMGame game = MMGames.getPlugin().getGame(args[0]);
      List<Object> drops = new ArrayList<Object>();
      if (game.getConfig().isSet("Drops")) {
         drops.addAll(game.getConfig().getList("Drops"));
      }
      drops.add(Utils.getString(player.getPlayer().getLocation()));
      game.getConfig().set("Drops", drops);
      game.saveConfig();
      player.sendMessage("[a]Added drop!");
      return true;
   }

}
