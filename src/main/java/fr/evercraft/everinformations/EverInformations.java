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
import fr.evercraft.everinformations.automessage.ManagerAutoMessage;
import fr.evercraft.everinformations.connection.ManagerConnection;
import fr.evercraft.everinformations.nametag.ManagerNameTag;
import fr.evercraft.everinformations.newbie.ManagerNewbie;
import fr.evercraft.everinformations.scoreboard.ManagerScoreBoard;
import fr.evercraft.everinformations.tablist.ManagerTabList;

@Plugin(id = "fr.evercraft.everinformations", 
		name = "EverInformations", 
		version = "1.1", 
		description = "Manage informations",
		url = "http://evercraft.fr/",
		authors = {"rexbut"},
		dependencies = {
		    @Dependency(id = "fr.evercraft.everapi", version = "1.1"),
		    @Dependency(id = "fr.evercraft.informations", version = "1.1", optional = true)
		})
public class EverInformations extends EPlugin {
	private EIConfig configs;
	
	private EIMessage messages;	
	
	private ManagerAutoMessage automessages;
	private ManagerConnection connection;
	private ManagerNewbie newbie;
	
	private ManagerScoreBoard scoreboard;
	private ManagerNameTag nametag;
	private ManagerTabList tablist;
	
	@Override
	protected void onPreEnable() {		
		this.configs = new EIConfig(this);
		
		this.messages = new EIMessage(this);
		
		this.getGame().getEventManager().registerListeners(this, new EIListener(this));
	}

	@Override
	protected void onEnable() {
		this.automessages = new ManagerAutoMessage(this);
		this.connection = new ManagerConnection(this);
		this.newbie = new ManagerNewbie(this);
		
		this.nametag = new ManagerNameTag(this);
		this.tablist = new ManagerTabList(this);
	}
	
	@Override
	protected void onCompleteEnable() {
		new EICommand(this);
	}
	
	@Override
	protected void onStartServer() {
		this.scoreboard = new ManagerScoreBoard(this);
	}


	protected void onReload(){
		this.reloadConfigurations();
		
		this.automessages.reload();
		this.connection.reload();
		this.newbie.reload();
		this.scoreboard.reload();
		this.nametag.reload();
		this.tablist.reload();
	}
	
	protected void onDisable() {
	}

	/*
	 * Accesseurs
	 */	
	public EIMessage getMessages(){
		return this.messages;
	}
	
	public EIConfig getConfigs() {
		return this.configs;
	}

	public ManagerAutoMessage getAutoMessages() {
		return this.automessages;
	}

	public ManagerConnection getConnection() {
		return this.connection;
	}
	
	public ManagerNewbie getNewbie() {
		return this.newbie;
	}
	
	public ManagerScoreBoard getScoreBoard() {
		return this.scoreboard;
	}
	
	public ManagerNameTag getNameTag() {
		return this.nametag;
	}
	
	public ManagerTabList getTabList() {
		return this.tablist;
	}
}
