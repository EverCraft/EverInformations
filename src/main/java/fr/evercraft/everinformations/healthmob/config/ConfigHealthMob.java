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
package fr.evercraft.everinformations.healthmob.config;
 
import java.util.Arrays;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;

public class ConfigHealthMob extends EConfig {
	public ConfigHealthMob(final EverInformations plugin) {
		super(plugin, "health_mob");
	}
	
	@Override
	protected void loadDefault() {
		addDefault("enable", true);
		addDefault("message", "<HEALTH> &4❤");
		addDefault("messages", Arrays.asList(
				"&c▌                   ",
				"&c▌                   ",
				"&c█                  ",
				"&c█▌                 ",
				"&c██                ",
				"&e██▌               ",
				"&e███              ",
				"&e███▌             ",
				"&e████            ",
				"&e████▌           ",
				"&e█████          ",
				"&a█████▌         ",
				"&a██████        ",
				"&a██████▌       ",
				"&a███████      ",
				"&a███████▌     ",
				"&a████████    ",
				"&a████████▌   ",
				"&a█████████  ",
				"&a█████████▌ ",
				"&a██████████"));
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
