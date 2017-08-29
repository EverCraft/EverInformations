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
import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.server.player.EPlayer;

public class ActionBarMessage implements IMessage {
	// En secondes
	private final double stay;
	private final double interval;
	
	private final String message;

	public ActionBarMessage(final double stay, double interval, String message) {
		this.stay = stay;
		this.interval = interval;
		this.message = message;
	}
	
	@Override
	public long getNext() {
		return (long) ((this.stay + this.interval) * 1000);
	}
	
	public long getStay() {
		return (long) (this.stay * 1000);
	}

	@Override
	public String toString() {
		return "ActionBarMessage [stay=" + stay + ", next=" + interval
				+ ", message=" + message + "]";
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player) {
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(player.getReplaces()));
	}
	
	@Override
	public boolean send(String identifier, int priority, EPlayer player, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(player.getReplaces());
		replaces.put(Pattern.compile("<reason>"), EReplace.of(reason));
		
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(replaces));
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace) {
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(replace.getReplaces()));
	}

	@Override
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason) {
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(replace.getReplaces());
		replaces.put(Pattern.compile("<reason>"), EReplace.of(reason));
		
		return player.sendActionBar(identifier, priority, this.getStay(), EFormatString.of(this.message).toText(replaces));
	}
}
