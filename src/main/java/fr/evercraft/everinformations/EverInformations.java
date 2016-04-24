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
package fr.evercraft.everinformations;

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everinformations.automessages.actionbar.ActionBarAutoMessages;
import fr.evercraft.everinformations.automessages.chat.ChatAutoMessages;
import fr.evercraft.everinformations.automessages.title.TitleAutoMessages;
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
	
	private ChatAutoMessages automessageChat;
	private ActionBarAutoMessages automessageActionBar;
	private TitleAutoMessages automessageTitle;
	
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
		this.automessageChat = new ChatAutoMessages(this);
		this.automessageActionBar = new ActionBarAutoMessages(this);
		this.automessageTitle = new TitleAutoMessages(this);
	}
	
	@Override
	protected void onCompleteEnable() {
		new EICommand(this);
	}

	protected void onReload(){
		this.reloadConfigurations();
		
		this.automessageChat.reload();
		this.automessageActionBar.reload();
		this.automessageTitle.reload();
		
		this.service.reload();
	}
	
	protected void onDisable() {
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
