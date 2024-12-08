package org.akazukin.library.command;

public interface ICmdSender {
    void sendMessage(String msg);

    CommandExecutor getExecutor();
}
