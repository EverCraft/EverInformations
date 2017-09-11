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
package fr.evercraft.everinformations;

import java.util.Arrays;
import java.util.List;

import fr.evercraft.everapi.plugin.file.EConfig;

public class EIConfig extends EConfig<EverInformations> {

	public EIConfig(final EverInformations plugin) {
		super(plugin);
	}
	
	public void reload() {
		super.reload();
		this.plugin.getELogger().setDebug(this.isDebug());
	}
	
	@Override
	public List<String> getHeader() {
		return 	Arrays.asList(	"####################################################### #",
								"               EverInformation (By rexbut)               #",
								"    For more information : https://docs.evercraft.fr     #",
								"####################################################### #");
	}
	
	@Override
	public void loadDefault() {
		this.configDefault();
		
		addDefault("newbie-and-connection", false, "Allows you to view welcome and login messages at the same time for new players");
		
		addDefault("vanish-connection-fake", true, "Allows to display false logout and logon messages when a player active or deserts his vanish");
	}
	
	/**
	 * Afficher les deux messages : première connexion et de connexion
	 * @return True si on affiche les deux messages
	 */
	public boolean isNewbieAndConnection() {
		return this.get("newbie-and-connection").getBoolean(false);
	}

	/**
	 * Afficher des faux messages lors qu'on active ou désactive le mode vanish
	 * @return True si on affiche les messages
	 */
	public boolean isVanishFake() {
		return this.get("vanish-connection-fake").getBoolean(true);
	}
}
