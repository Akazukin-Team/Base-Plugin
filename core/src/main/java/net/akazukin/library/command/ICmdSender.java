package net.akazukin.library.command;

public interface ICmdSender {
    void sendMessage(String msg);

    CommandExecutor getExecutor();
}
