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
package fr.evercraft.everinformations.automessages;

import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.automessages.actionbar.ActionBarAutoMessages;
import fr.evercraft.everinformations.automessages.chat.ChatAutoMessages;
import fr.evercraft.everinformations.automessages.title.TitleAutoMessages;

public class ManagerAutoMessages {
	private final EverInformations plugin;
	
	private final ActionBarAutoMessages actionbar;
	private final ChatAutoMessages chat;
	private final TitleAutoMessages title;
	
	public ManagerAutoMessages(final EverInformations plugin) {
		this.plugin = plugin;
		
		this.chat = new ChatAutoMessages(this.plugin);
		this.actionbar = new ActionBarAutoMessages(this.plugin);
		this.title = new TitleAutoMessages(this.plugin);
	}
	
	public void reload() {
		this.chat.reload();
		this.actionbar.reload();
		this.title.reload();
	}
}
