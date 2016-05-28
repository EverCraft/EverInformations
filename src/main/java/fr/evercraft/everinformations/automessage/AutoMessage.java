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
package fr.evercraft.everinformations.automessage;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.priority.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.automessage.AutoMessage;
import fr.evercraft.everinformations.automessage.config.IConfig;
import fr.evercraft.everinformations.message.IMessage;

public class AutoMessage<T extends IMessage> {
	public static enum Type {
    	ACTION_BAR,
    	TITLE,
    	CHAT;
    }
	
	public final static String IDENTIFIER = "everinformations.automessages";
	
	private final EverInformations plugin;
	
	private final Type type;
	
	private boolean enable;
	private int numero;
	
	private final IConfig<T> config;
	private final CopyOnWriteArrayList<T> messages;
	
	private int priority;
	private Task task;

	public AutoMessage(final EverInformations plugin, IConfig<T> config, Type type) {
		this.plugin = plugin;
		
		this.enable = false;
		this.numero = 0;
		
		this.type = type;
		
		this.config = config;
		this.messages = new CopyOnWriteArrayList<T>();
		
		reload();
	}

	public void reload(){		
		stop();

		this.priority = PriorityService.DEFAULT;
		if(this.plugin.getEverAPI().getManagerService().getPriority().isPresent()) {
			if(type.equals(Type.ACTION_BAR)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getActionBar(IDENTIFIER);
			} else if(type.equals(Type.TITLE)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTitle(IDENTIFIER);
			}
		}
		
		this.numero = 0;
		this.enable = this.config.isEnable();
		this.messages.clear();
		this.messages.addAll(this.config.getMessages());
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("AutoMessages (type='" + this.type.name() + "') : There is no message");
			this.enable = false;
			this.stop();
		} else if (this.enable && !this.plugin.getGame().getServer().getOnlinePlayers().isEmpty()) {
			this.start();
			this.plugin.getLogger().debug("AutoMessages (type='" + this.type.name() + "') : Start (player='" + this.plugin.getGame().getServer().getOnlinePlayers().size() + "')");
		} else {
			this.plugin.getLogger().debug("AutoMessages (type='" + this.type.name() + "') : No start (player='0')");
		}
	}

	public void start() {
		this.stop();
		this.view();
		this.task();
	}

	public void stop() {
		// Si start
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}
	
	public void task() {
		T message = this.getMessage();
		
		// Si il n'y a pas de délai
		if(message.getNext() == 0) {
			this.next();
		// Il y a un délai
		} else {
			this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
							.execute(() -> this.next())
							.async()
							.delay(message.getNext(), TimeUnit.MILLISECONDS)
							.name("AutoMessages (type='" + this.type.name() + "')")
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
			T message = this.getMessage();
			this.plugin.getLogger().debug("AutoMessages (type='" + this.type.name() + "';priority='" + this.priority + "';actionBar='" + message + "')");
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				message.send(this.priority, player);
			}
		}
	}
	
	public T getMessage() {
		return this.messages.get(this.numero);
	}
}
