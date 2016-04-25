/**
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

import java.util.Optional;

import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import fr.evercraft.everapi.server.player.EPlayer;

public class ChatMessage implements IMessage {
	// En Secondes
	private final double next;
	
	private final TextSerializer type;
	
	private final Optional<String> prefix;
	private final String message;
	
	public ChatMessage(final double next, final TextSerializer type, final String prefix, final String message) {
		this.next = next;
		this.type = type;
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
	public boolean send(int priority, EPlayer player) {
		if(this.type.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessageVariables(this.prefix.orElse("") + this.message);
		} else {
			if(this.prefix.isPresent()) {
				player.sendMessage(player.replaceVariable(this.prefix.get()).concat(this.type.deserialize(this.message)));
			} else {
				player.sendMessage(this.type.deserialize(this.message));
			}
		}
		return true;
	}
	
	@Override
	public void send(int priority, EPlayer player, EPlayer replace) {
		if(this.type.equals(TextSerializers.FORMATTING_CODE)) {
			player.sendMessage(replace.replaceVariable(this.prefix.orElse("") + this.message));
		} else {
			if(this.prefix.isPresent()) {
				player.sendMessage(replace.replaceVariable(this.prefix.get()).concat(this.type.deserialize(this.message)));
			} else {
				player.sendMessage(this.type.deserialize(this.message));
			}
		}
	}

	@Override
	public String toString() {
		return "ActionBarMessage [next=" + next  + ", message=" + message + "]";
	}
}
