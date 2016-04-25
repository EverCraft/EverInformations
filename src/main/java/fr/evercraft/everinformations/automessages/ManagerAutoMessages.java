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
import fr.evercraft.everinformations.automessages.AutoMessages.Type;
import fr.evercraft.everinformations.automessages.config.ConfigActionBar;
import fr.evercraft.everinformations.automessages.config.ConfigChat;
import fr.evercraft.everinformations.automessages.config.ConfigTitle;
import fr.evercraft.everinformations.message.ActionBarMessage;
import fr.evercraft.everinformations.message.ChatMessage;
import fr.evercraft.everinformations.message.TitleMessage;

public class ManagerAutoMessages {
	private final EverInformations plugin;
	

	private final AutoMessages<ChatMessage> chat;
	private final AutoMessages<ActionBarMessage> actionbar;
	private final AutoMessages<TitleMessage> title;
	
	public ManagerAutoMessages(final EverInformations plugin) {
		this.plugin = plugin;
		
		this.chat = new AutoMessages<ChatMessage>(this.plugin, new ConfigChat(this.plugin), Type.CHAT);
		this.actionbar = new AutoMessages<ActionBarMessage>(this.plugin, new ConfigActionBar(this.plugin), Type.ACTION_BAR);
		this.title = new AutoMessages<TitleMessage>(this.plugin, new ConfigTitle(this.plugin), Type.TITLE);
	}
	
	public void reload() {
		this.chat.reload();
		this.actionbar.reload();
		this.title.reload();
	}
}
