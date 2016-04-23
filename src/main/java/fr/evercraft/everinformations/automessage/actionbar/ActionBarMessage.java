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
package fr.evercraft.everinformations.automessage.actionbar;

public class ActionBarMessage  {
	private final int stay;
	private final int next;
	
	private final String message;

	public ActionBarMessage(final int stay, int next, String message) {
		this.stay = stay;
		this.next = next;
		this.message = message;
	}

	public int getStay() {
		return stay;
	}

	public int getNext() {
		return next;
	}

	public String getMessage() {
		return message;
	}
}
