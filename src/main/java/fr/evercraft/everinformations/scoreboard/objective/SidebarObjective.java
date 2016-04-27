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
package fr.evercraft.everinformations.scoreboard.objective;

import java.util.List;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everinformations.scoreboard.objective.sidebar.SidebarTitle;

public abstract class SidebarObjective extends EObjective {
	public static enum Type {
		INFORMATIONS,
		ECONOMY;
    }
	
	protected final Type type;
	
	protected final List<SidebarTitle> titles;

	
	public SidebarObjective(final EPlugin plugin, final double stay, final double update, Type type, List<SidebarTitle> titles) {
		super(plugin, stay, update);
		this.type = type;
		this.titles = titles;
	}
	
	@Override
	public long getNext() {
		return (long) (this.stay * 1000);
	}

	@Override
	public String toString() {
		return "SidebarObjective [type=" + type.name() + ", stay=" + stay + "]";
	}
}
