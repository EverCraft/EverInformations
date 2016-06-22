package fr.evercraft.everinformations.automessage.config;

import java.util.List;

import fr.evercraft.everinformations.message.IMessage;

public interface IConfig<T extends IMessage> {
	public boolean isEnable();
	public List<T> getMessages();
}
