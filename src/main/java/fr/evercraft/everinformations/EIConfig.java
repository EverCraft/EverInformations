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

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.plugin.file.EMessage;

public class EIConfig extends EConfig<EverInformations> {

	public EIConfig(final EverInformations plugin) {
		super(plugin);
	}
	
	public void reload() {
		super.reload();
		this.plugin.getELogger().setDebug(this.isDebug());
	}
	
	@Override
	public void loadDefault() {
		addDefault("DEBUG", false, "Displays plugin performance in the logs");
		addDefault("LANGUAGE", EMessage.FRENCH, "Select language messages", "Examples : ", "  French : FR_fr", "  English : EN_en");
		
		addDefault("newbie-and-connection", true);
		
		addDefault("vanish-connection-fake", true);
	}
	
	/**
	 * Afficher les deux messages : première connexion et de connexion
	 * @return True si on affiche les deux messages
	 */
	public boolean isNewbieAndConnection() {
		return this.get("player-newbie-and-connection").getBoolean(false);
	}

	/**
	 * Afficher des faux messages lors qu'on active ou désactive le mode vanish
	 * @return True si on affiche les messages
	 */
	public boolean isVanishFake() {
		return this.get("vanish-connection-fake").getBoolean(true);
	}
}
