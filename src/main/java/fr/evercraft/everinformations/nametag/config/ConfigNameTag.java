package fr.evercraft.everinformations.nametag.config;
 
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;

public class ConfigNameTag extends EConfig {
	public ConfigNameTag(final EverInformations plugin) {
		super(plugin, "nametag");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("prefix", "prefix", "PermissionService : Option");
		addDefault("suffix", "suffix", "PermissionService : Option");
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public String getPrefix() {
		return this.get("prefix").getString("prefix");
	}
	
	public String getSuffix() {
		return this.get("suffix").getString("suffix");
	}
}
