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

import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplace;
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
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(player.getReplaces()));
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.putAll(player.getReplaces());
		replaces.put("<reason>", EReplace.of(reason));
		
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(replaces));
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace) {
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(replace.getReplaces()));
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		Map<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
		replaces.putAll(replace.getReplaces());
		replaces.put("<reason>", EReplace.of(reason));
		
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(replaces));
	}
}
