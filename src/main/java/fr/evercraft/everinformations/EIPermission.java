package fr.evercraft.everinformations;

import fr.evercraft.everapi.plugin.EPermission;
import fr.evercraft.everapi.plugin.EPlugin;

public class EIPermission extends EPermission {

	public EIPermission(final EPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void load() {
		add("EVERINFORMATIONS", "command");
		
		add("HELP", "help");
		add("RELOAD", "reload");
	}
}
