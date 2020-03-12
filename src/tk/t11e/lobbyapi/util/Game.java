package tk.t11e.lobbyapi.util;
// Created by booky10 in LobbyAPIBungee (20:50 02.03.20)

import com.sun.istack.internal.NotNull;

import java.util.UUID;

public class Game {

    private String name, material, server, pluginMessage;
    private UUID id;
    private Type type;

    public Game(@NotNull String name, @NotNull UUID id, @NotNull String material, String server,
                @NotNull Type type, String pluginMessage) {
        this.name = name;
        this.id = id;
        this.material = material;
        this.server = server;
        this.type = type;
        this.pluginMessage = pluginMessage;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public String getMaterial() {
        return material;
    }

    public String getServer() {
        return server;
    }

    public Type getType() {
        return type;
    }

    public String getPluginMessage() {
        return pluginMessage;
    }

    public Game setId(UUID id) {
        this.id = id;
        return this;
    }

    public Game setMaterial(String material) {
        this.material = material;
        return this;
    }

    public Game setName(String name) {
        this.name = name;
        return this;
    }

    public Game setPluginMessage(String pluginMessage) {
        this.pluginMessage = pluginMessage;
        return this;
    }

    public Game setServer(String server) {
        this.server = server;
        return this;
    }

    public Game setType(Type type) {
        this.type = type;
        return this;
    }

    public enum Type {
        OTHER,
        ACTIVE,
        PASSIVE;
    }
}