package fr.evercraft.everinformations.newbie;

import java.util.concurrent.CopyOnWriteArrayList;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.PriorityService;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.IMessage;
import fr.evercraft.everinformations.newbie.config.IConfig;

public abstract class Newbie<T extends IMessage> {
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
	
	public final static String PLAYER = "player";
	public final static String OTHERS = "others";
	
	public final static String IDENTIFIER_PLAYER = "everinformations.newbie.player";
	public final static String IDENTIFIER_OTHERS = "everinformations.newbie.others";
	
	protected final EverInformations plugin;

	protected final IConfig<T> config;
	
	protected final CopyOnWriteArrayList<T> messages;
	
	protected boolean enable;
	protected final Type type;
	protected int priority;

	public Newbie(final EverInformations plugin, final IConfig<T> config, final Type type) {	
		this.plugin = plugin;
		this.config = config;
		this.type = type;
		
		this.messages = new CopyOnWriteArrayList<T>();
	}

	public abstract void reload();
	
	public abstract void stop();

	public abstract void addPlayer(EPlayer player);
	public abstract void removePlayer(EPlayer player);
	
	protected void loadPriority() {
		this.priority = PriorityService.DEFAULT;
		if(this.plugin.getEverAPI().getManagerService().getPriority().isPresent()) {
			if(type.equals(Type.ACTION_BAR_PLAYER)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getActionBar(Newbie.IDENTIFIER_PLAYER);
			} else if(type.equals(Type.ACTION_BAR_OTHERS)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getActionBar(Newbie.IDENTIFIER_OTHERS);
			} else if(type.equals(Type.TITLE_PLAYER)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTitle(Newbie.IDENTIFIER_PLAYER);
			} else if(type.equals(Type.TITLE_OTHERS)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTitle(Newbie.IDENTIFIER_OTHERS);
			} else if(type.equals(Type.BOSSBAR_PLAYER)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTitle(Newbie.IDENTIFIER_PLAYER);
			} else if(type.equals(Type.BOSSBAR_OTHERS)) {
				this.priority = this.plugin.getEverAPI().getManagerService().getPriority().get().getTitle(Newbie.IDENTIFIER_OTHERS);
			}
		}
	}
}
