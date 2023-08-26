package land.esta.macrocheck.listeners;

import land.esta.macrocheck.MacroCheck;
import land.esta.macrocheck.utils.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class InfoListener implements Listener {
    private final MacroCheck plugin;

    public InfoListener(final MacroCheck instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().trim().equalsIgnoreCase("/macrochecker info")) {
            e.getPlayer().sendMessage(CC.translate("&4&m--*------------------------------*-"));
            e.getPlayer().sendMessage(CC.translate("&c&l Info:"));
            e.getPlayer().sendMessage(CC.translate("&c  ● &fName: &cMacro Check "));
            e.getPlayer().sendMessage(CC.translate("&c  ● &fAuthors: "));
            e.getPlayer().sendMessage(CC.translate("&c    &4▶ &ctdvne"));
            e.getPlayer().sendMessage(CC.translate("&c  ● &fVersion: &cv" + plugin.getDescription().getVersion()));
            e.getPlayer().sendMessage(CC.translate("&c  ● &fWebsite: &c" + plugin.getDescription().getWebsite()));
            e.getPlayer().sendMessage(CC.translate("&c  ● &fGithub: &cGithub.com/tdvne/MacroChecker"));
            e.getPlayer().sendMessage(CC.translate("&4&m--*------------------------------*-"));
            e.setCancelled(true);
        }
    }
}