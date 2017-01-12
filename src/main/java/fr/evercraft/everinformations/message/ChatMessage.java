/*
 * This file is part of EverInformations.
 *
 * EverInformations is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverInformations is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverInformations.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.everinformations.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplace;
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
		
		if (prefix.isEmpty()) {
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
		if (this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(EFormatString.of(this.prefix.orElse("") + this.message).toText(player.getReplacesPlayer()));
		} else {
			player.sendMessage(EChat.of(this.prefix.orElse("")).concat(this.format.deserialize(this.message)));
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.putAll(player.getReplacesPlayer());
		replaces.put("<reason>", EReplace.of(reason));
		
		if (this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(EFormatString.of(this.prefix.orElse("") + this.message).toText(replaces));
		} else {
			player.sendMessage(EChat.of(this.prefix.orElse("")).concat(this.format.deserialize(this.message)));
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace) {
		if (this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(EFormatString.of(this.prefix.orElse("") + this.message).toText(replace.getReplacesPlayer()));
		} else {
			player.sendMessage(EChat.of(this.prefix.orElse("")).concat(this.format.deserialize(this.message)));
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.putAll(replace.getReplacesPlayer());
		replaces.put("<reason>", EReplace.of(reason));
		
		if (this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(EFormatString.of(this.prefix.orElse("") + this.message).toText(replaces));
		} else {
			player.sendMessage(EChat.of(this.prefix.orElse("")).concat(this.format.deserialize(this.message)));
		}
		return true;
	}

	@Override
	public String toString() {
		return "ActionBarMessage [next=" + next  + ", message=" + message + "]";
	}
}
