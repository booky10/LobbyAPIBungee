package tk.t11e.lobbyapi.main;
// Created by booky10 in LobbyAPIBungee (18:41 02.03.20)

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import tk.t11e.lobbyapi.manager.LobbyManager;

import java.util.concurrent.TimeUnit;

public class Main extends Plugin {

    public static final String PREFIX = "§7[§bT11E§7]§c ", NO_PERMISSION = PREFIX + "You don't have " +
            "the permissions for this!";
    public static Main main;
    public static ProxyServer proxy;

    @Override
    public void onEnable() {
        long milliseconds = System.currentTimeMillis();
        main = this;
        proxy = getProxy();

        getProxy().registerChannel("lobby:api");
        init();
        proxy.getScheduler().schedule(this, () -> proxy.getScheduler().runAsync(Main.main, () -> {
            for (ServerInfo server : proxy.getServersCopy().values())
                LobbyManager.sendPluginMessageGames(server, LobbyManager.SendType.SCHEDULED_GAME_SYNC,
                        LobbyManager.getGames());
        }), 2, TimeUnit.MINUTES);

        milliseconds = System.currentTimeMillis() - milliseconds;
        getLogger().info("It took " + milliseconds + "ms to initialize this plugin!");
    }

    private void init() {
        getProxy().getPluginManager().registerListener(this, new LobbyManager());
    }
}