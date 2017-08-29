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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.InformationService.Priorities;
import fr.evercraft.everapi.services.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.connection.config.IConfig;
import fr.evercraft.everinformations.message.IMessage;

public abstract class Connection<T extends IMessage> {
	public static enum Connections {
    	JOIN,
    	QUIT,
    	KICK;
    }
	
	public static enum Type {
    	ACTION_BAR_PLAYER,
    	ACTION_BAR_OTHERS,
    	TITLE_PLAYER,
    	TITLE_OTHERS,
    	BOSSBAR_PLAYER,
    	BOSSBAR_OTHERS,
    	CHAT_PLAYER,
    	CHAT_OTHERS;
    }
	
	public final static String DEFAULT = "DEFAULT";
	
	public final static String PLAYER = "player";
	public final static String OTHERS = "others";
	
	protected final EverInformations plugin;

	protected final IConfig<T> config;
	
	protected final ConcurrentMap<String, List<T>> messages_join;
	protected final ConcurrentMap<String, List<T>> messages_quit;
	protected final ConcurrentMap<String, List<T>> messages_kick;
	
	protected boolean enable;
	protected final Type type;
	protected int priority;

	public Connection(final EverInformations plugin, final IConfig<T> config, final Type type) {	
		this.plugin = plugin;
		this.config = config;
		this.type = type;
		
		this.messages_join = new ConcurrentHashMap<String, List<T>>();
		this.messages_quit = new ConcurrentHashMap<String, List<T>>();
		this.messages_kick = new ConcurrentHashMap<String, List<T>>();
	}

	public abstract void reload();
	public abstract void stop();

	public abstract void joinPlayer(EPlayer player, Optional<SubjectReference> optional);
	public abstract void quitPlayer(EPlayer player, Optional<SubjectReference> subject);
	public abstract void kickPlayer(EPlayer player, Optional<SubjectReference> subject, Text text);
	
	protected void loadPriority() {
		this.priority = PriorityService.PRIORITY_DEFAULT;
		if (type.equals(Type.ACTION_BAR_PLAYER)) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get(PriorityService.ACTIONBAR, Priorities.CONNECTION_PLAYER);
		} else if (type.equals(Type.ACTION_BAR_OTHERS)) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get(PriorityService.ACTIONBAR, Priorities.CONNECTION_OTHERS);
		} else if (type.equals(Type.TITLE_PLAYER)) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get(PriorityService.TITLE, Priorities.CONNECTION_PLAYER);
		} else if (type.equals(Type.TITLE_OTHERS)) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get(PriorityService.TITLE, Priorities.CONNECTION_OTHERS);
		} else if (type.equals(Type.BOSSBAR_PLAYER)) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get(PriorityService.BOSSBAR, Priorities.CONNECTION_PLAYER);
		} else if (type.equals(Type.BOSSBAR_OTHERS)) {
			this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get(PriorityService.BOSSBAR, Priorities.CONNECTION_OTHERS);
		}
	}
	
	protected List<T> getMessagesJoin(Optional<SubjectReference> subject) {
		return getMessages(this.messages_join, subject);
	}
	
	protected List<T> getMessagesQuit(Optional<SubjectReference> subject) {
		return getMessages(this.messages_quit, subject);
	}
	
	protected List<T> getMessagesKick(Optional<SubjectReference> subject) {
		return getMessages(this.messages_kick, subject);
	}
	
	protected List<T> getMessages(Map<String, List<T>> map, Optional<SubjectReference> subject) {
		if (subject.isPresent() && map.containsKey(subject.get().getSubjectIdentifier())) {
			return map.get(subject.get().getSubjectIdentifier());
		}
		if (map.containsKey(Connection.DEFAULT)) {
			return map.get(Connection.DEFAULT);
		}
		return Arrays.asList();
	}

	public boolean isEnable() {
		return this.enable;
	}
}
