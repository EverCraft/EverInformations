package fr.evercraft.everinformations.newbie;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ActionBarMessage;
import fr.evercraft.everinformations.message.BossBarMessage;
import fr.evercraft.everinformations.message.ChatMessage;
import fr.evercraft.everinformations.message.TitleMessage;
import fr.evercraft.everinformations.newbie.Newbie.Type;
import fr.evercraft.everinformations.newbie.config.ConfigActionBar;
import fr.evercraft.everinformations.newbie.config.ConfigBossBar;
import fr.evercraft.everinformations.newbie.config.ConfigChat;
import fr.evercraft.everinformations.newbie.config.ConfigTitle;
import fr.evercraft.everinformations.newbie.config.IConfig;

public class ManagerNewbie {
	private final EverInformations plugin;
	
	private final Newbie<ChatMessage> chatPlayer;
	private final Newbie<ChatMessage> chatOthers;
	
	private final Newbie<TitleMessage> titlePlayer;
	private final Newbie<TitleMessage> titleOthers;
	
	private final Newbie<BossBarMessage> bossbarPlayer;
	private final Newbie<BossBarMessage> bossbarOthers;

	private final Newbie<ActionBarMessage> actionbarPlayer;
	private final Newbie<ActionBarMessage> actionbarOthers;
	
	public ManagerNewbie(final EverInformations plugin) {
		this.plugin = plugin;
		
		IConfig<ChatMessage> config_chat = new ConfigChat(this.plugin);
		this.chatPlayer = new NewbiePlayer<ChatMessage>(this.plugin, config_chat, Type.CHAT_PLAYER);
		this.chatOthers = new NewbiePlayer<ChatMessage>(this.plugin, config_chat, Type.CHAT_OTHERS);
		
		IConfig<TitleMessage> config_title = new ConfigTitle(this.plugin);
		this.titlePlayer = new NewbiePlayer<TitleMessage>(this.plugin, config_title, Type.TITLE_PLAYER);
		this.titleOthers = new NewbieOthers<TitleMessage>(this.plugin, config_title, Type.TITLE_OTHERS);
		
		IConfig<BossBarMessage> config_bossbar = new ConfigBossBar(this.plugin);
		this.bossbarPlayer = new NewbiePlayer<BossBarMessage>(this.plugin, config_bossbar, Type.BOSSBAR_PLAYER);
		this.bossbarOthers = new NewbieOthers<BossBarMessage>(this.plugin, config_bossbar, Type.BOSSBAR_OTHERS);
		
		IConfig<ActionBarMessage> config_actionbar = new ConfigActionBar(this.plugin);
		this.actionbarPlayer = new NewbiePlayer<ActionBarMessage>(this.plugin, config_actionbar, Type.ACTION_BAR_PLAYER);
		this.actionbarOthers = new NewbieOthers<ActionBarMessage>(this.plugin, config_actionbar, Type.ACTION_BAR_OTHERS);
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
	
	public void stop() {
		this.chatPlayer.stop();
		this.chatOthers.stop();
		
		this.titlePlayer.stop();
		this.titleOthers.stop();
		
		this.bossbarPlayer.stop();
		this.bossbarOthers.stop();
		
		this.actionbarPlayer.stop();
		this.actionbarOthers.stop();
	}

	public void addPlayer(EPlayer player) {
		this.chatPlayer.addPlayer(player);
		this.chatOthers.addPlayer(player);
		
		this.titlePlayer.addPlayer(player);
		this.titleOthers.addPlayer(player);
		
		this.bossbarPlayer.addPlayer(player);
		this.bossbarOthers.addPlayer(player);
		
		this.actionbarPlayer.addPlayer(player);
		this.actionbarOthers.addPlayer(player);
	}
	
	public void removePlayer(EPlayer player) {
		this.chatPlayer.removePlayer(player);
		this.chatOthers.removePlayer(player);
		
		this.titlePlayer.removePlayer(player);
		this.titleOthers.removePlayer(player);
		
		this.bossbarPlayer.removePlayer(player);
		this.bossbarOthers.removePlayer(player);
		
		this.actionbarPlayer.removePlayer(player);
		this.actionbarOthers.removePlayer(player);
	}
}
