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
