package fr.evercraft.everinformations;

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.services.chat.event.ChatSystemEvent;
import fr.evercraft.everinformations.service.EInformationsService;

@Plugin(id = "fr.evercraft.everinformations", 
		name = "EverInformations", 
		version = "1.0", 
		description = "Manage information",
		url = "http://evercraft.fr/",
		authors = {"rexbut"},
		dependencies = {
		    @Dependency(id = "fr.evercraft.everapi", version = "1.0")
		})
public class EverInformations extends EPlugin {
	private EIConfig configs;
	
	private EIMessage messages;
	private EIPermission permissions;
	
	private EInformationsService service;
	
	@Override
	protected void onPreEnable() {
		this.permissions = new EIPermission(this);
		
		this.configs = new EIConfig(this);
		
		this.messages = new EIMessage(this);
		
		this.getGame().getEventManager().registerListeners(this, new EIListener(this));
		
		this.service = new EInformationsService(this);
		//this.getGame().getServiceManager().setProvider(this, InformationsService.class, this.service);
	}

	@Override
	protected void onEnable() {
		this.postEvent(ChatSystemEvent.Action.RELOADED);
	}
	
	@Override
	protected void onCompleteEnable() {
		new EICommand(this);
	}

	protected void onReload(){
		this.reloadConfigurations();
		this.service.reload();
		this.postEvent(ChatSystemEvent.Action.RELOADED);
	}
	
	protected void onDisable() {
	}
	
	private void postEvent(ChatSystemEvent.Action action) {
		this.getLogger().debug("Event ChatSystemEvent : (Action='" + action.name() +"')");
		this.getGame().getEventManager().post(new ChatSystemEvent(this, action));
	}

	/*
	 * Accesseurs
	 */
	public EIPermission getPermissions() {
		return this.permissions;
	}
	
	public EIMessage getMessages(){
		return this.messages;
	}
	
	public EIConfig getConfigs() {
		return this.configs;
	}

	public EInformationsService getService() {
		return this.service;
	}
}
