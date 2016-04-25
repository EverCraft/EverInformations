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
package fr.evercraft.everinformations.newbie;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;

public abstract class Newbie {
	public final static String PLAYER = "player";
	public final static String OTHERS = "others";
	
	public final static String IDENTIFIER_PLAYER = "everinformations.newbie.player";
	public final static String IDENTIFIER_OTHER = "everinformations.newbie.others";
	
	protected final EverInformations plugin;

	protected boolean enable;

	public Newbie(final EverInformations plugin) {	
		this.plugin = plugin;
	}

	public abstract void reload();
	
	public abstract void stop();

	public abstract void addPlayer(EPlayer player);
	public abstract void removePlayer(EPlayer player);
}
