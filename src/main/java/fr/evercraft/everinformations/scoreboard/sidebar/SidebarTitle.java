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
package fr.evercraft.everinformations.scoreboard.sidebar;

import fr.evercraft.everapi.message.format.EFormatString;

public class SidebarTitle  {
	// En secondes
	private final double stay;
	
	private final EFormatString title;
	
	public SidebarTitle(final double stay, EFormatString title) {
		this.stay = stay;
		this.title = title;
	}

	public long getNext() {
		return (long) (this.stay * 1000);
	}
	
	public EFormatString getTitle() {
		return this.title;
	}

	@Override
	public String toString() {
		return "SidebarTitle [stay=" + stay + ", title=" + title + "]";
	}
	
	
}
