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

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.automessage.AutoMessages;

public class ActionBarAutoMessages extends AutoMessages {
	private final ActionBarConfig config;
	
	private long last_update;
	private Task task_update;
	private int update;
	
	private final CopyOnWriteArrayList<ActionBarMessage> messages;
	private Text prefix;

	public ActionBarAutoMessages(final EverInformations plugin) {
		super(plugin);
		
		this.config = new ActionBarConfig(this.plugin);
		this.messages = new CopyOnWriteArrayList<ActionBarMessage>();
		
		reload();
	}

	public void reload(){		
		stop();
		init();
	}

	protected void init() {
		this.numero = -1;
		
		this.enable = this.config.isEnable();
		this.update = this.config.getUpdate();
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
		
		if (this.task_update != null) {
			this.task_update.cancel();
		}
		if (this.task != null) {
			this.task.cancel();
		}
		
		ActionBarMessage message = this.getMessage();
		
		this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
					.execute(() -> {
						this.view();
						this.next();
					})
					.async()
					.delay(message.getNext() + message.getStay(), TimeUnit.SECONDS)
					.name("AutoMessage ActionBar")
					.submit(this.plugin);
		
		this.last_update = System.currentTimeMillis() + (message.getStay() * 1000);
		
		this.running = true;
	}

	public void stop() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.running = false;
	}
	
	public ActionBarMessage getMessage() {
		return this.messages.get(this.numero);
	}

	public void next() {
		if (this.task_update != null) {
			this.task_update.cancel();
		}
		if (this.task != null) {
			this.task.cancel();
		}
		
		this.numero++;
		if(this.numero >= this.messages.size()){
			this.numero = 0;
		}
		
		this.view();
		ActionBarMessage message = this.getMessage();
		
		this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
					.execute(() -> {
						this.next();
					})
					.async()
					.delay(message.getNext() + message.getStay(), TimeUnit.SECONDS)
					.name("AutoMessage ActionBar")
					.submit(this.plugin);
	}

	protected void view() {
		if(this.enable) {
			this.plugin.getLogger().debug("AutoMessagesChat (message='" + this.getMessage() + "')");
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				player.sendMessage(ChatTypes.ACTION_BAR, this.plugin.getChat().replaceAllVariables(player, this.getMessage().getMessage()));
			}
			
			if(System.currentTimeMillis() + this.update < this.last_update) {
				this.task_update = this.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> {
							this.view();
						})
						.async()
						.delay(this.update, TimeUnit.SECONDS)
						.name("AutoMessage ActionBar : Update")
						.submit(this.plugin);
			} else if (this.last_update - System.currentTimeMillis() > 0){
				this.task_update = this.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> {
							for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
								player.sendMessage(ChatTypes.ACTION_BAR, Text.of());
							}
						})
						.async()
						.delay(this.update - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.name("AutoMessage ActionBar : Last update")
						.submit(this.plugin);
			} else {
				for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
					player.sendMessage(ChatTypes.ACTION_BAR, Text.of());
				}
			}
		}
	}
}
