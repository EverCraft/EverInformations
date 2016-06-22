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
package fr.evercraft.everinformations.automessage;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.automessage.AutoMessage.Type;
import fr.evercraft.everinformations.automessage.config.ConfigActionBar;
import fr.evercraft.everinformations.automessage.config.ConfigBossBar;
import fr.evercraft.everinformations.automessage.config.ConfigChat;
import fr.evercraft.everinformations.automessage.config.ConfigTitle;
import fr.evercraft.everinformations.message.ActionBarMessage;
import fr.evercraft.everinformations.message.BossBarMessage;
import fr.evercraft.everinformations.message.ChatMessage;
import fr.evercraft.everinformations.message.TitleMessage;

public class ManagerAutoMessage {

	private final EverInformations plugin;
	

	private final AutoMessage<ChatMessage> chat;
	private final AutoMessage<ActionBarMessage> actionbar;
	private final AutoMessage<TitleMessage> title;
	private final AutoMessage<BossBarMessage> bossbar;
	
	public ManagerAutoMessage(final EverInformations plugin) {
		this.plugin = plugin;
		
		this.chat = new AutoMessage<ChatMessage>(this.plugin, new ConfigChat(this.plugin), Type.CHAT);
		this.actionbar = new AutoMessage<ActionBarMessage>(this.plugin, new ConfigActionBar(this.plugin), Type.ACTION_BAR);
		this.title = new AutoMessage<TitleMessage>(this.plugin, new ConfigTitle(this.plugin), Type.TITLE);
		this.bossbar = new AutoMessage<BossBarMessage>(this.plugin, new ConfigBossBar(this.plugin), Type.BOSSBAR);
	}
	
	public void reload() {
		this.chat.reload();
		this.actionbar.reload();
		this.title.reload();
		this.bossbar.reload();
	}
	
	public void start() {
		this.chat.start();
		this.actionbar.start();
		this.title.start();
		this.bossbar.start();
	}

	public void stop() {
		this.chat.stop();
		this.actionbar.stop();
		this.title.stop();
		this.bossbar.stop();
	}
	
	public void addPlayer(EPlayer player) {
		this.bossbar.addPlayer(player);
	}
	
	public void removePlayer(EPlayer player) {
		this.bossbar.removePlayer(player);
	}
	
	public void eventActionBar(EPlayer player, String before_identifier) {
		if(!before_identifier.equalsIgnoreCase(AutoMessage.IDENTIFIER)) {
			this.actionbar.addPlayer(player);
		}
	}

	public void eventTitle(EPlayer player, String before_identifier) {
		if(!before_identifier.equalsIgnoreCase(AutoMessage.IDENTIFIER)) {
			this.title.addPlayer(player);
		}
	}
	
	public void eventBossBar(EPlayer player, String before_identifier) {
		if(!before_identifier.equalsIgnoreCase(AutoMessage.IDENTIFIER)) {
			this.bossbar.addPlayer(player);
		}
	}
}
