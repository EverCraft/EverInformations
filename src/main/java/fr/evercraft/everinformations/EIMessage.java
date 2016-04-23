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
package fr.evercraft.everinformations;

import fr.evercraft.everapi.plugin.file.EMessage;

public class EIMessage extends EMessage {

	public EIMessage(final EverInformations plugin) {
		super(plugin);
	}

	@Override
	public void loadDefault() {
		// Prefix
		addDefault("prefix", "[&4Ever&6&lInformations&f] ");
	}

	@Override
	public void loadConfig() {
		// Prefix
		addMessage("PREFIX", "prefix");
	}
}
