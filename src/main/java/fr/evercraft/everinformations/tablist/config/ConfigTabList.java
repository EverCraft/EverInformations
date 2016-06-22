package fr.evercraft.everinformations.tablist.config;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.tablist.TabListMessage;

public class ConfigTabList extends EConfig {
	public ConfigTabList(final EverInformations plugin) {
		super(plugin, "tablist");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("update", 20);
		addDefault("header", "&4Header[RT]&aSubHeader");
		addDefault("footer", "&4Footer[RT]&aSubFooter");
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
	
	public List<TabListMessage> getTabLists() {
		List<TabListMessage> tablists = new ArrayList<TabListMessage>();
		
		double stay_default = this.get("stay").getDouble(60);
		double update_default = this.get("update").getDouble(20);
		
		// Objectif unique
		if(this.get("tablists").isVirtual()) {
			String header = this.plugin.getChat().replace(this.get("header").getString(""));
			String footer = this.plugin.getChat().replace(this.get("footer").getString(""));
			tablists.add(new TabListMessage((EverInformations) this.plugin, stay_default, update_default, header, footer));
		// Liste d'objectives
		} else {
			for(ConfigurationNode config : this.get("objectives").getChildrenList()) {
				double stay = config.getNode("stay").getDouble(stay_default);
				double update = config.getNode("update").getDouble(update_default);
				
				String header = this.plugin.getChat().replace(config.getNode("header").getString(""));
				String footer = this.plugin.getChat().replace(config.getNode("footer").getString(""));
				
				tablists.add(new TabListMessage((EverInformations) this.plugin, stay, update, header, footer));
			}
		}
		return tablists;
	}
}
