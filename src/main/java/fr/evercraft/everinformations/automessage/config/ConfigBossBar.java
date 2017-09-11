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
package fr.evercraft.everinformations.automessage.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.BossBarOverlays;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.sponge.UtilsBossBar;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.BossBarMessage;

public class ConfigBossBar extends EConfig<EverInformations> implements IConfig<BossBarMessage> {

	public ConfigBossBar(final EverInformations plugin) {
		super(plugin, "automessage/automessage_bossbar");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", false);
		
		addDefault("stay", 360, "Seconds");
		addDefault("interval", 0, "Seconds");
		addDefault("percent", 100, "0-100");
		addDefault("color", "BLUE", "Colors : BLUE|GREEN|PINK|PURPLE|RED|WHITE|YELLOW");
		addDefault("overlay", "PROGRESS", "Overlay : NOTCHED_6|NOTCHED_10|NOTCHED_12|NOTCHED_20|PROGRESS");
		addDefault("darkenSky", false);
		addDefault("playEndBossMusic", false);
		addDefault("createFog", false);
		addDefault("messages", Arrays.asList("&1Message 1 ......", "&bMessage 2 ......", "&cMessage 3 ......", "&aMessage 4 ......"));
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isEnable() {
		return this.get("enable").getBoolean(false);
	}
	
	public List<BossBarMessage> getMessages() {
		List<BossBarMessage> messages = new ArrayList<BossBarMessage>();
		
		// Default
		double stay_default = this.get("stay").getDouble(360);
		double next_default = this.get("interval").getDouble(0);
		float percent_default = this.get("percent").getFloat(100);
		BossBarColor color_default = UtilsBossBar.getColor(this.get("color").getString("")).orElse(BossBarColors.WHITE);
		BossBarOverlay overlay_default = UtilsBossBar.getOverlay(this.get("overlay").getString("")).orElse(BossBarOverlays.PROGRESS);
		boolean darkenSky_default = this.get("darkenSky").getBoolean(false);
		boolean playEndBossMusic_default = this.get("playEndBossMusic").getBoolean(false);
		boolean createFog_default = this.get("createFog").getBoolean(false);
		
		for (ConfigurationNode config : this.get("messages").getChildrenList()) {
			// Message uniquement
			if (config.getValue() instanceof String) {
				String message = this.plugin.getChat().replace(config.getString(""));
				
				if (!message.isEmpty()) {
					messages.add(new BossBarMessage(stay_default, next_default, message, percent_default, color_default, overlay_default,
							darkenSky_default, playEndBossMusic_default, createFog_default));
				}
			// Message avec config
			} else {
				double stay = config.getNode("stay").getDouble(stay_default);
				double next = config.getNode("next").getDouble(config.getNode("interval").getDouble(next_default));
				float percent = config.getNode("percent").getFloat(percent_default);
				BossBarColor color = UtilsBossBar.getColor(config.getNode("color").getString("")).orElse(color_default);
				BossBarOverlay overlay = UtilsBossBar.getOverlay(config.getNode("overlay").getString("")).orElse(overlay_default);
				boolean darkenSky = config.getNode("darkenSky").getBoolean(darkenSky_default);
				boolean playEndBossMusic = config.getNode("playEndBossMusic").getBoolean(playEndBossMusic_default);
				boolean createFog = config.getNode("createFog").getBoolean(createFog_default);
				
				String message = this.plugin.getChat().replace(config.getNode("message").getString(""));

				if (!message.isEmpty()) {
					messages.add(new BossBarMessage(stay, next, message, percent, color, overlay,
							darkenSky, playEndBossMusic, createFog));
				}
			}
		}
		return messages;
	}
}
