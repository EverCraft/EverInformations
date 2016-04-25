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
package fr.evercraft.everinformations.automessages.chat;

import java.util.concurrent.CopyOnWriteArrayList;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.automessages.AutoMessages;
import fr.evercraft.everinformations.message.ChatMessage;

public class ChatAutoMessages extends AutoMessages {
	private final ChatConfig config;
	
	private final CopyOnWriteArrayList<ChatMessage> messages;

	public ChatAutoMessages(final EverInformations plugin) {
		super(plugin);
		
		this.config = new ChatConfig(this.plugin);
		this.messages = new CopyOnWriteArrayList<ChatMessage>();
		
		reload();
	}

	public void reload(){		
		stop();
		
		this.numero = 0;
		
		this.enable = this.config.isEnable();
		this.messages.clear();
		this.messages.addAll(this.config.getMessages());
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("ChatAutoMessages : There is no message");
			this.enable = false;
			stop();
		} else if (this.enable) {
			start();
		}
	}

	public void start() {
		this.stop();
		this.view();
		this.task();
	}

	public void stop() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}
	
	public void task() {
		ChatMessage message = this.getMessage();
		
		if(message.getNext() == 0) {
			this.next();
		} else {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.execute(() -> this.next())
							.async()
							.delayTicks(message.getNext())
							.name("ChatAutoMessages")
							.submit(this.plugin);
		}
	}
	
	public void next() {		
		this.numero++;
		if(this.numero >= this.messages.size()){
			this.numero = 0;
		}
		this.view();
		this.task();
	}

	protected void view() {
		if(this.enable) {
			ChatMessage message = this.getMessage();
			this.plugin.getLogger().debug("ChatAutoMessages (message='" + message + "')");

			
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				message.send(player);
			}
		}
	}
	
	public ChatMessage getMessage() {
		return this.messages.get(this.numero);
	}
}
