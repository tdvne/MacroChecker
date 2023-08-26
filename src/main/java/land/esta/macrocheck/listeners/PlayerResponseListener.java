package land.esta.macrocheck.listeners;


import land.esta.macrocheck.MacroCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerResponseListener implements Listener {

    private MacroCheck plugin;

    public PlayerResponseListener(MacroCheck plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player executor = event.getPlayer();
        if (plugin.isPlayerBeingChecked(event.getPlayer())) {
            handleResponse(event.getPlayer(), true, executor);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player executor = plugin.getExecutor(event.getPlayer());
        if(executor != null) {
            handleResponse(event.getPlayer(), false, executor);
        }
    }


    private void handleResponse(Player player, boolean passed, Player executor) {
        plugin.stopChecking(player, passed, executor);
    }
}