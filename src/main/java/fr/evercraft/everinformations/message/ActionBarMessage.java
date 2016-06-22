package fr.evercraft.everinformations.message;

import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;

public class ActionBarMessage implements IMessage {
	// En secondes
	private final double stay;
	private final double next;
	
	private final String message;

	public ActionBarMessage(final double stay, double next, String message) {
		this.stay = stay;
		this.next = next;
		this.message = message;
	}
	
	public long getStay() {
		return (long) (this.stay * 1000);
	}

	@Override
	public long getNext() {
		return (long) ((this.next + this.stay) * 1000);
	}

	@Override
	public String toString() {
		return "ActionBarMessage [stay=" + stay + ", next=" + next
				+ ", message=" + message + "]";
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player) {
		return player.sendActionBar(identifier, priority, this.getStay(), player.replaceVariable(this.message));
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		return player.sendActionBar(identifier, priority, this.getStay(), player.replaceVariable(this.message.replaceAll("<reason>", EChat.serialize(reason))));
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace) {
		return player.sendActionBar(identifier, priority, this.getStay(), replace.replaceVariable(this.message));
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		return player.sendActionBar(identifier, priority, this.getStay(), replace.replaceVariable(this.message.replaceAll("<reason>", EChat.serialize(reason))));
	}
}
