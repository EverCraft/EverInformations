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
package fr.evercraft.everinformations.connection;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.InformationService.Priorities;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.connection.config.IConfig;
import fr.evercraft.everinformations.message.BossBarMessage;
import fr.evercraft.everinformations.message.IMessage;

public class ConnectionOthers<T extends IMessage> extends Connection<T> {
	protected List<T> messages;
	
	private EPlayer player;
	private Text reason;
	private int numero;
	private Task task;
	
	public ConnectionOthers(final EverInformations plugin, final IConfig<T> config, Type type) {
		super(plugin, config, type);
		
		reload();
	}

	public void reload(){		
		stop();
		
		this.loadPriority();
		this.numero = 0;
		this.enable = this.config.isOthersEnable();
		this.messages_join.clear();
		this.messages_quit.clear();
		this.messages_kick.clear();
		
		this.messages_join.putAll(this.config.getOthersJoinMessages());
		this.messages_quit.putAll(this.config.getOthersQuitMessages());
		this.messages_kick.putAll(this.config.getOthersKickMessages());
		
		if (this.enable && this.messages_join.isEmpty() && this.messages_quit.isEmpty() && this.messages_kick.isEmpty()) {
			this.plugin.getELogger().warn("Connection (type='" + this.type.name() + "') : There is no message");
			this.enable = false;
		}
	}

	public void start() {
		// Enable et n'est pas Start
		if (this.enable && this.task == null && !this.messages.isEmpty()) {
			this.numero = -1;
			this.next();
		}
	}

	public void stop() {
		this.remove();
		
		// Si Start
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
		this.player = null;
		this.reason = null;
	}
	
	public void next() {		
		this.numero++;
		this.view();
		
		// Si il y a encore un message
		if (this.numero < this.messages.size() - 1) {
			this.task();
		}
	}
	
	protected void view() {
		T message = this.getMessage();
		this.plugin.getELogger().debug("Connection (type='" + this.type.name() + "';priority='" + this.priority + "';message='" + message + "')");

		if (this.reason == null) {
			// Affiche le message à tous les autres joueurs
			for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if (!this.player.equals(player)) {
					message.send(Priorities.CONNECTION_OTHERS, this.priority, player, this.player);
				}
			}
		} else {
			// Affiche le message à tous les autres joueurs
			for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if (!this.player.equals(player)) {
					message.send(Priorities.CONNECTION_OTHERS, this.priority, player, this.player, this.reason);
				}
			}
		}
	}
	
	public void task() {
		T message = this.getMessage();
		
		// Si il y a pas de délai
		if (message.getNext() <= 0) {
			this.next();
		// Il y a un délai
		} else {
			this.taskNext();
		}
	}
	
	public void taskNext() {
		T message = this.getMessage();
		this.task = this.plugin.getGame().getScheduler().createTaskBuilder()
				.execute(() -> this.next())
				.async()
				.delay(message.getNext(), TimeUnit.MILLISECONDS)
				.name("Connection : Next (type='" + this.type.name() + "')")
				.submit(this.plugin);
	}
	
	public T getMessage() {
		return this.messages.get(this.numero);
	}

	@Override
	public void joinPlayer(EPlayer player, Optional<SubjectReference> subject) {
		if (this.enable) {
			this.stop();
			this.player = player;
			this.messages = getMessagesJoin(subject);
			this.start();
		}
	}

	@Override
	public void quitPlayer(EPlayer player, Optional<SubjectReference> subject) {
		if (this.enable) {
			this.stop();
			this.player = player;
			this.messages = getMessagesQuit(subject);
			this.start();
		}
	}
	
	@Override
	public void kickPlayer(EPlayer player, Optional<SubjectReference> subject, Text reason) {
		if (this.enable) {
			this.stop();
			this.player = player;
			this.messages = getMessagesKick(subject);
			this.reason = reason;
			this.start();
		}
	}
	
	public void remove() {
		if (this.enable && this.player != null && this.messages != null && !this.messages.isEmpty() && this.getMessage() instanceof BossBarMessage) {
			BossBarMessage message = ((BossBarMessage) this.getMessage());
			
			this.plugin.getELogger().debug("Connection : RemoveBossbar (type='" + this.type.name() + "';priority='" + this.priority + "';message='" + message + "')");
			for (EPlayer player : this.plugin.getEServer().getOnlineEPlayers()) {
				if (!this.player.equals(player)) {
					message.remove(Priorities.CONNECTION_OTHERS, player);
				}
			}
		}
	}
}
