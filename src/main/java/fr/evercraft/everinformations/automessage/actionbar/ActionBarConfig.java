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
package fr.evercraft.everinformations.automessage.actionbar;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;

public class ActionBarConfig extends EConfig {

	public ActionBarConfig(final EverInformations plugin) {
		super(plugin, "automessages_actionbar");
	}
	
	public void reload() {
		super.reload();
	}
	
	@Override
	public void loadDefault() {
		addDefault("enable", true);
		addDefault("interval", 300);
		addDefault("stay", 300);
		addDefault("update", 8);
		addDefault("messages", Arrays.asList("&1[ARROW] Message 1 ......", "&bMessage 2 ......", "&cMessage 3 ......", "&aMessage 4 ......"));
	}
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public int getInterval() {
		return this.get("interval").getInt(300);
	}
	
	public int getUpdate() {
		return this.get("update").getInt(8);
	}
	
	public Text getStay() {
		return EChat.of(this.plugin.getChat().replace(this.get("prefix").getString("&f[&4Ever&6&lNews&f] ")));
	}
	
	public List<ActionBarMessage> getMessages() {
		return null;
	}
}
