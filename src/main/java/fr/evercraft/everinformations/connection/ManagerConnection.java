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
package fr.evercraft.everinformations.connection;

import java.util.Optional;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.connection.Connection.Type;
import fr.evercraft.everinformations.connection.config.ConfigActionBar;
import fr.evercraft.everinformations.connection.config.ConfigBossBar;
import fr.evercraft.everinformations.connection.config.ConfigChat;
import fr.evercraft.everinformations.connection.config.ConfigTitle;
import fr.evercraft.everinformations.connection.config.IConfig;
import fr.evercraft.everinformations.message.ActionBarMessage;
import fr.evercraft.everinformations.message.BossBarMessage;
import fr.evercraft.everinformations.message.ChatMessage;
import fr.evercraft.everinformations.message.TitleMessage;
public class ManagerConnection {
	private final EverInformations plugin;
	
	private final Connection<ChatMessage> chatPlayer;
	private final Connection<ChatMessage> chatOthers;
	
	private final Connection<TitleMessage> titlePlayer;
	private final Connection<TitleMessage> titleOthers;
	
	private final Connection<BossBarMessage> bossbarPlayer;
	private final Connection<BossBarMessage> bossbarOthers;

	private final Connection<ActionBarMessage> actionbarPlayer;
	private final Connection<ActionBarMessage> actionbarOthers;
	
	public ManagerConnection(final EverInformations plugin) {
		this.plugin = plugin;
		
		IConfig<ChatMessage> config_chat = new ConfigChat(this.plugin);
		this.chatPlayer = new ConnectionPlayer<ChatMessage>(this.plugin, config_chat, Type.CHAT_PLAYER);
		this.chatOthers = new ConnectionPlayer<ChatMessage>(this.plugin, config_chat, Type.CHAT_OTHERS);
		
		IConfig<TitleMessage> config_title = new ConfigTitle(this.plugin);
		this.titlePlayer = new ConnectionPlayer<TitleMessage>(this.plugin, config_title, Type.TITLE_PLAYER);
		this.titleOthers = new ConnectionOthers<TitleMessage>(this.plugin, config_title, Type.TITLE_OTHERS);
		
		IConfig<BossBarMessage> config_bossbar = new ConfigBossBar(this.plugin);
		this.bossbarPlayer = new ConnectionPlayer<BossBarMessage>(this.plugin, config_bossbar, Type.BOSSBAR_PLAYER);
		this.bossbarOthers = new ConnectionOthers<BossBarMessage>(this.plugin, config_bossbar, Type.BOSSBAR_OTHERS);
		
		IConfig<ActionBarMessage> config_actionbar = new ConfigActionBar(this.plugin);
		this.actionbarPlayer = new ConnectionPlayer<ActionBarMessage>(this.plugin, config_actionbar, Type.ACTION_BAR_PLAYER);
		this.actionbarOthers = new ConnectionOthers<ActionBarMessage>(this.plugin, config_actionbar, Type.ACTION_BAR_OTHERS);
	}
	
	public void reload() {
		this.chatPlayer.reload();
		this.chatOthers.reload();
		
		this.titlePlayer.reload();
		this.titleOthers.reload();
		
		this.bossbarPlayer.reload();
		this.bossbarOthers.reload();
		
		this.actionbarPlayer.reload();
		this.actionbarOthers.reload();
	}

	public void joinPlayer(EPlayer player, Optional<Subject> subject) {
		this.chatPlayer.joinPlayer(player, subject);
		this.chatOthers.joinPlayer(player, subject);
		
		this.titlePlayer.joinPlayer(player, subject);
		this.titleOthers.joinPlayer(player, subject);
		
		this.bossbarPlayer.joinPlayer(player, subject);
		this.bossbarOthers.joinPlayer(player, subject);
		
		this.actionbarPlayer.joinPlayer(player, subject);
		this.actionbarOthers.joinPlayer(player, subject);
	}
	
	public void quitPlayer(EPlayer player, Optional<Subject> subject) {
		this.chatPlayer.quitPlayer(player, subject);
		this.chatOthers.quitPlayer(player, subject);
		
		this.titlePlayer.quitPlayer(player, subject);
		this.titleOthers.quitPlayer(player, subject);
		
		this.bossbarPlayer.quitPlayer(player, subject);
		this.bossbarOthers.quitPlayer(player, subject);
		
		this.actionbarPlayer.quitPlayer(player, subject);
		this.actionbarOthers.quitPlayer(player, subject);
	}
	
	public void kickPlayer(EPlayer player, Optional<Subject> subject, Text reason) {
		this.chatPlayer.kickPlayer(player, subject, reason);
		this.chatOthers.kickPlayer(player, subject, reason);
		
		this.titlePlayer.kickPlayer(player, subject, reason);
		this.titleOthers.kickPlayer(player, subject, reason);
		
		this.bossbarPlayer.kickPlayer(player, subject, reason);
		this.bossbarOthers.kickPlayer(player, subject, reason);
		
		this.actionbarPlayer.kickPlayer(player, subject, reason);
		this.actionbarOthers.kickPlayer(player, subject, reason);
	}
	
	public boolean isEnableChat() {
		return this.chatPlayer.isEnable() || this.chatPlayer.isEnable();
	}
}
