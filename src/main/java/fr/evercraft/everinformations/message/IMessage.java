package fr.evercraft.everinformations.message;

import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.server.player.EPlayer;

public interface IMessage  {
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getNext();

	public boolean send(String identifier, int priority, EPlayer player);
	
	public boolean send(String identifier, int priority, EPlayer player, Text reason);

	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace);
	
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason);
}
