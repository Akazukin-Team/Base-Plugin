package net.akazukin.library.command;

import java.util.UUID;

public interface PlayerWrapper {
    String getDisplayName();

    String getName();

    UUID getUniqueId();

    void sendMessage(String msg);

    void kick(String reason);

    void ban();

    void banIPs();

    String[] getIPs();

    String getLastIP();
}
