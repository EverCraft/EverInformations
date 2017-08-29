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
import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.server.player.EPlayer;

public class ChatMessage implements IMessage {
	// En Secondes
	private final double interval;
	
	private final TextSerializer format;
	
	private final Optional<String> prefix;
	private final String message;
	
	public ChatMessage(final double interval, final TextSerializer format, final String prefix, final String message) {
		this.interval = interval;
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
		return (long) (this.interval * 1000);
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player) {
		if (this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(EFormatString.of(this.prefix.orElse("") + this.message).toText(player.getReplaces()));
		} else {
			player.sendMessage(EChat.of(this.prefix.orElse("")).concat(this.format.deserialize(this.message)));
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(player.getReplaces());
		replaces.put(Pattern.compile("<reason>"), EReplace.of(reason));
		
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
			player.sendMessage(EFormatString.of(this.prefix.orElse("") + this.message).toText(replace.getReplaces()));
		} else {
			player.sendMessage(EChat.of(this.prefix.orElse("")).concat(this.format.deserialize(this.message)));
		}
		return true;
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(replace.getReplaces());
		replaces.put(Pattern.compile("<reason>"), EReplace.of(reason));
		
		if (this.format.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(EFormatString.of(this.prefix.orElse("") + this.message).toText(replaces));
		} else {
			player.sendMessage(EChat.of(this.prefix.orElse("")).concat(this.format.deserialize(this.message)));
		}
		return true;
	}

	@Override
	public String toString() {
		return "ActionBarMessage [interval=" + interval  + ", message=" + message + "]";
	}
}
