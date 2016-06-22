package fr.evercraft.everinformations.connection.config;

import java.util.List;
import java.util.Map;

import fr.evercraft.everinformations.message.IMessage;

public interface IConfig<T extends IMessage> {
	public boolean isPlayerEnable();
	public Map<String, List<T>> getPlayerJoinMessages();
	
	public boolean isOthersEnable();
	public Map<String, List<T>> getOthersJoinMessages();
	public Map<String, List<T>> getOthersQuitMessages();
	public Map<String, List<T>> getOthersKickMessages();
}
