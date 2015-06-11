package me.paulbgd.mmgames;

import java.util.ArrayList;
import java.util.List;

import me.paulbgd.mmgames.game.GameType;

import org.bukkit.Material;

public class Settings {
   
   public static boolean COMMAND_JOIN = true;

   public static class WorldSettings {
      public static boolean TIME_CHANGE = false;
      public static final List<Material> blocksBreakable = new ArrayList<Material>();
   }
   
   public static class GameSettings {
      public static GameType GAME_TYPE = GameType.PLAYER;
      public static boolean HUNGER = false;
      public static boolean RESPAWN_AFTER_DEATH = true;
      public static boolean RESPAWN_WHEN_OVER = true;
      public static boolean START_IN_SPAWNPOINTS = false;
      public static boolean PLAY_JOIN_FIREWORK = false;
      public static int TIMER = 30;
      public static long DROP_TIMER = 0;
      public static final List<Material> blocksBreakable = new ArrayList<Material>();
   }
   
}
