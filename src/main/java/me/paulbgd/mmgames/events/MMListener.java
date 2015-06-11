package me.paulbgd.mmgames.events;

public abstract class MMListener {

   private ListenerType type;

   public MMListener(ListenerType type) {
      this.type = type;
   }

   public abstract boolean onEvent(Object[] data);

   public ListenerType getType() {
      return this.type;
   }

   public enum ListenerType {
      RIGHT_CLICK, LEFT_CLICK, ITEM_HIT, JOIN_GAME, LEAVE_GAME, KILL_ENTITY, KILL_PLAYER, PLAYER_RESPAWN, GAME_START, GAME_END, TNT_HIT, START_SPECTATING, HIT_PLAYER, JOIN_SERVER;
   }

}
