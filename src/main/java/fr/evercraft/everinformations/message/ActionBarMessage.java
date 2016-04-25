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

import fr.evercraft.everapi.server.player.EPlayer;

public class ActionBarMessage  {
	private final long stay;
	private final long next;
	
	private final String message;

	public ActionBarMessage(final long stay, long next, String message) {
		this.stay = stay;
		this.next = next;
		this.message = message;
	}

	public long getStay() {
		return this.stay;
	}

	public long getNext() {
		return this.next;
	}

	public String getMessage() {
		return this.message;
	}

	@Override
	public String toString() {
		return "ActionBarMessage [stay=" + stay + ", next=" + next
				+ ", message=" + message + "]";
	}

	public boolean send(int priority, EPlayer player) {
		player.sendActionBar(priority, this.stay, player.replaceVariable(this.message));
		return true;
	}

	public void send(int priority, EPlayer player, EPlayer replace) {
		player.sendActionBar(priority, this.stay, replace.replaceVariable(this.message));
	}
}
