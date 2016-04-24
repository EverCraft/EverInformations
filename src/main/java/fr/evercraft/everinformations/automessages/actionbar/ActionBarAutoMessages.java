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
package fr.evercraft.everinformations.automessages.actionbar;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.automessages.AutoMessages;

public class ActionBarAutoMessages extends AutoMessages {
	private final static String IDENTIFIER = "everinformation.automessages";
	
	private final ActionBarConfig config;
	
	private final CopyOnWriteArrayList<ActionBarMessage> messages;

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
		this.numero = 0;
		
		this.enable = this.config.isEnable();
		this.messages.clear();
		this.messages.addAll(this.config.getMessages());
		
		if (this.messages.size() == 0 && this.enable) {
			this.plugin.getLogger().warn("AutoMessagesChat : There is no message");
			this.enable = false;
			this.stop();
		} else if (this.enable) {
			this.start();
		} else {
			this.stop();
		}
	}

	public void start() {
		this.stop();
		this.task();
	}

	public void stop() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}
	
	public void task() {
		ActionBarMessage message = this.getMessage();
		
		this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
						.execute(() -> this.next())
						.async()
						.delay(message.getNext() + message.getStay(), TimeUnit.MILLISECONDS)
						.name("AutoMessage ActionBar")
						.submit(this.plugin);
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
			ActionBarMessage message = this.getMessage();
			this.plugin.getLogger().debug("AutoMessagesActionBar (message='" + message.getMessage() + "')");
			for(EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				player.sendActionBar(IDENTIFIER, message.getStay(), this.plugin.getChat().replaceAllVariables(player, message.getMessage()));
			}
		}
	}
	
	public ActionBarMessage getMessage() {
		return this.messages.get(this.numero);
	}
}
