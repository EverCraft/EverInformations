package fr.evercraft.everinformations;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.plugin.file.EMessage;

public class EIConfig extends EConfig {

	public EIConfig(final EverInformations plugin) {
		super(plugin);
	}
	
	public void reload() {
		super.reload();
		this.plugin.getLogger().setDebug(this.isDebug());
	}
	
	@Override
	public void loadDefault() {
		addDefault("debug", false, "Displays plugin performance in the logs");
		addDefault("language", EMessage.ENGLISH, "Select language messages", "Examples : ", "  French : FR_fr", "  English : EN_en");
		
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
		return this.get("vanish-connection-fake").getBoolean(false);
	}
}
