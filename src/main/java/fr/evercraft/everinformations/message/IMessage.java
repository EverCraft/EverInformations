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

import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.server.player.EPlayer;

public interface IMessage  {
	
	/**
	 * En Millisecondes
	 * @return
	 */
	public long getNext();

	public boolean send(String identifier, int priority, EPlayer player);
	
	public boolean send(String identifier, int priority, EPlayer player, Text reason);

	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace);
	
	public boolean send(String identifier, int priority, EPlayer player, EPlayer replace, Text reason);
}
