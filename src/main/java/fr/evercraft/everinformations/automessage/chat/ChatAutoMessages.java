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
package fr.evercraft.everinformations.automessage.chat;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.automessage.AutoMessages;

public class ChatAutoMessages extends AutoMessages {
	private final ChatConfig config;
	
	private final CopyOnWriteArrayList<String> messages;
	private String prefix;
	
	private long interval;

	public ChatAutoMessages(final EverInformations plugin) {
		super(plugin);
		
		this.config = new ChatConfig(this.plugin);
		this.messages = new CopyOnWriteArrayList<String>();
		
		reload();
	}

	public void reload(){		
		stop();
		init();
	}

	protected void init() {
		this.numero = -1;
		
		this.enable = this.config.isEnable();
		this.interval = this.config.getInterval();
		this.prefix = this.config.getPrefix();
		this.messages.clear();
		this.messages.addAll(this.config.getMessages());
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("AutoMessagesChat : There is no message");
			this.enable = false;
			stop();
		} else if (this.enable) {
			start();
		} else {
			stop();
		}
	}

	public void start() {
		if (this.running) {
			stop();
		}
		
		this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
					.execute(() -> this.next())
					.async()
					.interval(this.interval, TimeUnit.SECONDS)
					.delay(this.interval, TimeUnit.SECONDS)
					.name("AutoMessage Chat")
					.submit(this.plugin);
		this.running = true;
	}

	public void stop() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.running = false;
	}
	
	public void next(){
		this.numero++;
		if(this.numero >= this.messages.size()){
			this.numero = 0;
		}
		view();
	}
	
	public void before(){
		this.numero--;
		if(this.numero < 0){
			this.numero = this.messages.size() - 1;
		}
		view();
	}

	public String getMessage(){
		return this.messages.get(this.numero);
	}
	
	protected void view() {
		if (this.enable) {
			this.plugin.getLogger().debug("AutoMessagesChat (message='" + this.prefix + this.getMessage() + "')");
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				player.sendMessage(this.plugin.getChat().replaceAllVariables(player, this.prefix + this.getMessage()));
			}
		}
	}
}
