package me.paulbgd.mmgames.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.player.MMPlayer;

import org.bukkit.entity.Player;

public class MMSQL {

   protected Connection connection;
   private MMGames plugin = MMGames.getPlugin();

   public MMPlayer loadPlayer(Player player) {
      return plugin.loadPlayer(new MMPlayer(player));
   }

   protected synchronized Connection getConnection() throws SQLException {
      if (null != connection) {
         if (connection.isValid(1)) {
            return connection;
         } else {
            connection.close();
         }
      }
      connection = DriverManager.getConnection(this.plugin.getConfig().getString("db_url"), this.plugin.getConfig().getString("db_user"), this.plugin.getConfig().getString("db_pass"));
      return connection;
   }

   public void closeConnection() {
      try {
         this.connection.close();
      } catch (final SQLException e) {
         MMGames.getPlugin().getLogger().log(Level.SEVERE, "Error while closing database connection:");
         e.printStackTrace();
      }
   }
   
}
