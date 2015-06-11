package me.paulbgd.mmgames.cmd;

import me.paulbgd.mmgames.MMGames;

import org.bukkit.command.CommandExecutor;

public abstract class BaseCommand implements CommandExecutor {

   private final String[] names;
   protected final MMGames plugin;

   public BaseCommand(String... names) {
      this.names = names;
      this.plugin = MMGames.getPlugin();
   }

   public String[] getNames() {
      return this.names;
   }

}
