package fr.evercraft.everinformations.message;

import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;

public class ChatMessage implements IMessage {
	// En Secondes
	private final double next;
	
	private final TextSerializer format;
	
	private final Optional<String> prefix;
	private final String message;
	
	public ChatMessage(final double next, final TextSerializer format, final String prefix, final String message) {
		this.next = next;
		this.format = format;
		this.message = message;
		
		if(prefix.isEmpty()) {
			this.prefix = Optional.empty();
		} else {
			this.prefix = Optional.ofNullable(prefix);
		}
	}

	@Override
	public long getNext() {
		return (long) (this.next * 1000);
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player) {
		// Format normal
		if(this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessageVariables(this.prefix.orElse("") + this.message);
		// Format différent
		} else {
			if(this.prefix.isPresent()) {
				player.sendMessage(player.replaceVariable(this.prefix.get()).concat(this.format.deserialize(this.message)));
			} else {
				player.sendMessage(this.format.deserialize(this.message));
			}
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		// Format normal
		if(this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessageVariables((this.prefix.orElse("") + this.message).replaceAll("<reason>", EChat.serialize(reason)));
		// Format différent
		} else {
			if(this.prefix.isPresent()) {
				player.sendMessage(player.replaceVariable(this.prefix.get()).concat(this.format.deserialize(this.message)));
			} else {
				player.sendMessage(this.format.deserialize(this.message));
			}
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace) {
		// Format normal
		if(this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(replace.replaceVariable(this.prefix.orElse("") + this.message));
		// Format différent
		} else {
			if(this.prefix.isPresent()) {
				player.sendMessage(replace.replaceVariable(this.prefix.get()).concat(this.format.deserialize(this.message)));
			} else {
				player.sendMessage(this.format.deserialize(this.message));
			}
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		// Format normal
		if(this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(replace.replaceVariable((this.prefix.orElse("") + this.message).replaceAll("<reason>", EChat.serialize(reason))));
		// Format différent
		} else {
			if(this.prefix.isPresent()) {
				player.sendMessage(replace.replaceVariable(this.prefix.get()).concat(this.format.deserialize(this.message)));
			} else {
				player.sendMessage(this.format.deserialize(this.message));
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "ActionBarMessage [next=" + next  + ", message=" + message + "]";
	}
}
