package org.akazukin.library.command;

import lombok.Getter;

@Getter
public abstract class SubCommand<T extends ICmdSender> extends Command<T> {
}
