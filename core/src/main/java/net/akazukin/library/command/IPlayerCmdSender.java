package net.akazukin.library.command;

import java.util.UUID;

public interface IPlayerCmdSender extends ICmdSender {
    String getDisplayName();

    String getName();

    UUID getUniqueId();

    boolean hasPermission(String perm);
}
