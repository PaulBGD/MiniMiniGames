package me.paulbgd.mmgames.utils;

import java.util.ArrayList;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.player.MMPlayer;

public class PlayerList extends ArrayList<MMPlayer> {

   private static final long serialVersionUID = 1L;

   @Override
   public MMPlayer get(int index) {
      MMPlayer player = super.get(index);
      if (MMGames.getPlugin().hasPlayer(player)) {
         player = MMGames.getPlugin().getPlayer(player.getName()); // refresh - just in case
      } else if (!player.getPlayer().isOnline()) {
         this.remove(index); // remove as it's gone
         return this.get(index); // rerun
      }
      return player;
   }

   public PlayerList addDynamic(MMPlayer player) {
      this.add(player);
      return this;
   }

}
