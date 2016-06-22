package fr.evercraft.everinformations.newbie.config;

import java.util.List;

import fr.evercraft.everinformations.message.IMessage;

public interface IConfig<T extends IMessage> {
	public boolean isPlayerEnable();
	public List<T> getPlayerMessages();
	
	public boolean isOthersEnable();
	public List<T> getOthersMessages();
}
