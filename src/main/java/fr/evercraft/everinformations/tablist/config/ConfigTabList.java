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
package fr.evercraft.everinformations.tablist.config;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.tablist.TabListMessage;

public class ConfigTabList extends EConfig<EverInformations> {
	public ConfigTabList(final EverInformations plugin) {
		super(plugin, "tablist");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true, "There is no character limit for prefix and suffix values ​​in the TabList as opposed to NameTag");
		addDefault("update", 20);
		addDefault("header", "&4Header[RT]&aSubHeader");
		addDefault("footer", "&4Footer[RT]&aSubFooter");
		
		addDefault("displayname.prefix", "prefix", "PermissionService : Option");
		addDefault("displayname.suffix", "suffix", "PermissionService : Option");
		addDefault("displayname.update", -1, "Seconds (-1 to disable updating)");
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public String getDisplayNamePrefix() {
		return this.get("displayname.prefix").getString("prefix");
	}
	
	public String getDisplayNameSuffix() {
		return this.get("displayname.suffix").getString("suffix");
	}
	
	public long getDisplayNameUpdate() {
		return this.get("displayname.update").getLong(-1);
	}
	
	public List<TabListMessage> getTabLists() {
		List<TabListMessage> tablists = new ArrayList<TabListMessage>();
		
		double stay_default = this.get("stay").getDouble(60);
		double update_default = this.get("update").getDouble(20);
		
		// Objectif unique
		if (this.get("tablists").isVirtual()) {
			String header = this.plugin.getChat().replace(this.get("header").getString(""));
			String footer = this.plugin.getChat().replace(this.get("footer").getString(""));
			tablists.add(new TabListMessage((EverInformations) this.plugin, stay_default, update_default, header, footer));
		// Liste d'objectives
		} else {
			for (ConfigurationNode config : this.get("objectives").getChildrenList()) {
				double stay = config.getNode("stay").getDouble(stay_default);
				double update = config.getNode("update").getDouble(update_default);
				
				String header = this.plugin.getChat().replace(config.getNode("header").getString(""));
				String footer = this.plugin.getChat().replace(config.getNode("footer").getString(""));
				
				tablists.add(new TabListMessage(this.plugin, stay, update, header, footer));
			}
		}
		return tablists;
	}
}
