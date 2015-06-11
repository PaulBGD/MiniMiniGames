package me.paulbgd.mmgames.player;

import org.bukkit.ChatColor;

public class Ranks {

   public enum Rank {
      DEFAULT(0, ChatColor.GRAY), VIP(1, ChatColor.GREEN), PRO(1, ChatColor.BLUE), YOUTUBER(1, ChatColor.RED), GM(2, ChatColor.AQUA), MGM(3, ChatColor.GOLD), ADMIN(3, ChatColor.GOLD), CM(4, ChatColor.RED), OWNER(5, ChatColor.DARK_RED), OP(4,
            ChatColor.DARK_RED), DEVELOPER(4, ChatColor.RED);

      private int level;
      private ChatColor color;

      private Rank(int level, ChatColor color) {
         this.level = level;
         this.color = color;
      }

      public int getLevel() {
         return this.level;
      }

      public ChatColor getColor() {
         return this.color;
      }

      public boolean is(Rank other) {
         return other.getLevel() <= this.getLevel();
      }

   }

}
