package me.paulbgd.mmgames.game;

import java.util.ArrayList;
import java.util.List;

import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.MMData;

public class MMTeam extends MMData {

   private final List<MMPlayer> players = new ArrayList<MMPlayer>();
   private final String name;

   public MMTeam(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void addPlayer(MMPlayer player) {
      this.players.add(player);
   }

   public MMTeam addPlayers(List<MMPlayer> players) {
      this.players.addAll(players);
      return this;
   }

   public List<MMPlayer> getPlayers() {
      return this.players;
   }

}
