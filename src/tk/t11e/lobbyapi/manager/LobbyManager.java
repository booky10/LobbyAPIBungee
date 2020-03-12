package tk.t11e.lobbyapi.manager;
// Created by booky10 in LobbyAPIBungee (20:40 02.03.20)

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.t11e.lobbyapi.main.Main;
import tk.t11e.lobbyapi.util.Game;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class LobbyManager implements Listener {

    private static List<Game> games = new ArrayList<>();

    public static void register(Game game) {
        Main.proxy.getScheduler().runAsync(Main.main, () -> {
            if (!games.contains(game) && !exitsId(game.getId()))
                games.add(game);
        });
    }

    public static List<Game> getGames() {
        return games;
    }

    public static Boolean exitsId(String id) {
        return exitsId(UUID.fromString(id));
    }

    public static Boolean exitsId(UUID id) {
        boolean exits = false;
        for (Game game : games)
            if (game.getId().equals(id)) {
                exits = true;
                break;
            }
        return exits;
    }

    public static void sendGames(ServerInfo receiver) {
        sendPluginMessageGames(receiver, SendType.ALL, games);
    }

    public static List<Game> getActiveGames() {
        List<Game> activeGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.ACTIVE))
                activeGames.add(game);
        return activeGames;
    }

    public static List<Game> getPassiveGames() {
        List<Game> passiveGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.PASSIVE))
                passiveGames.add(game);
        return passiveGames;
    }

    public static List<Game> getOtherGames() {
        List<Game> otherGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.OTHER))
                otherGames.add(game);
        return otherGames;
    }

    public static void sendActiveGames(ServerInfo receiver) {
        List<Game> activeGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.ACTIVE))
                activeGames.add(game);
        sendPluginMessageGames(receiver, SendType.ACTIVE, activeGames);
    }

    public static void sendPassiveGames(ServerInfo receiver) {
        List<Game> passiveGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.PASSIVE))
                passiveGames.add(game);
        sendPluginMessageGames(receiver, SendType.PASSIVE, passiveGames);
    }

    public static void sendOtherGames(ServerInfo receiver) {
        List<Game> otherGames = new ArrayList<>();
        for (Game game : games)
            if (game.getType().equals(Game.Type.OTHER))
                otherGames.add(game);
        sendPluginMessageGames(receiver, SendType.OTHER, otherGames);
    }

    public static void sendPluginMessageGames(ServerInfo receiver, SendType sendType, List<Game> games) {
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);

        try {
            dataOutput.writeUTF(sendType.getSubChannel());
            dataOutput.writeUTF("START");
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        receiver.sendData("lobby:api", byteArrayOutput.toByteArray(), false);

        for (Game game : games) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            try {
                dataOutputStream.writeUTF(sendType.getSubChannel());

                dataOutputStream.writeUTF(game.getName());
                dataOutputStream.writeUTF(game.getId().toString());
                dataOutputStream.writeUTF(game.getMaterial().toUpperCase());
                dataOutputStream.writeUTF(game.getServer());
                dataOutputStream.writeUTF(game.getType().toString().toUpperCase());
                dataOutputStream.writeUTF(game.getPluginMessage());
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }

            receiver.sendData("lobby:api", byteArrayOutputStream.toByteArray(), false);
        }
    }

    @EventHandler
    public void onMessageReceive(PluginMessageEvent event) {
        if (event.getTag().equals("lobby:api")) {

            ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(event.getData());
            String subChannel = byteArrayDataInput.readUTF();

            if (subChannel.equalsIgnoreCase("RegisterGame")) {
                String name = byteArrayDataInput.readUTF();
                UUID id = UUID.fromString(byteArrayDataInput.readUTF());
                String material = byteArrayDataInput.readUTF().toUpperCase();
                String server = byteArrayDataInput.readUTF();
                Game.Type type = Game.Type.valueOf(byteArrayDataInput.readUTF().toUpperCase());
                String pluginMessage = byteArrayDataInput.readUTF();

                Game game = new Game(name, id, material, server, type, pluginMessage);
                register(game);
            } else if (subChannel.equalsIgnoreCase("GetGames")) {
                sendGames(getServer(event.getReceiver()));
            } else if (subChannel.equalsIgnoreCase("GetActiveGames")) {
                sendActiveGames(getServer(event.getReceiver()));
            } else if (subChannel.equalsIgnoreCase("GetPassiveGames")) {
                sendPassiveGames(getServer(event.getReceiver()));
            } else if (subChannel.equalsIgnoreCase("GetOtherGames")) {
                sendOtherGames(getServer(event.getReceiver()));
            }
        }
    }

    private ServerInfo getServer(Connection connection) {
        for (ServerInfo serverInfo : Main.proxy.getServersCopy().values())
            for (ProxiedPlayer player : serverInfo.getPlayers())
                if (connection.getSocketAddress().equals(player.getSocketAddress()))
                    return serverInfo;
        return Main.proxy.getServersCopy().values().iterator().next();
    }

    public enum SendType {
        ACTIVE("GetActiveGames"),
        PASSIVE("GetPassiveGames"),
        OTHER("GetOtherGames"),
        ALL("GetGames"),
        SCHEDULED_GAME_SYNC("ScheduledGameSync");

        private String subChannel;

        SendType(String subChannel) {
            this.subChannel = subChannel;
        }

        public String getSubChannel() {
            return subChannel;
        }
    }
}