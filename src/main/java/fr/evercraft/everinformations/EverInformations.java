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
package fr.evercraft.everinformations;

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import fr.evercraft.everapi.EverAPI;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everinformations.automessage.ManagerAutoMessage;
import fr.evercraft.everinformations.command.sub.EIReload;
import fr.evercraft.everinformations.connection.ManagerConnection;
import fr.evercraft.everinformations.healthmob.ManagerHealthMob;
import fr.evercraft.everinformations.nametag.ManagerNameTag;
import fr.evercraft.everinformations.newbie.ManagerNewbie;
import fr.evercraft.everinformations.scoreboard.ManagerScoreBoard;
import fr.evercraft.everinformations.tablist.ManagerTabList;

@Plugin(id = "everinformations", 
		name = "EverInformations", 
		version = EverAPI.VERSION, 
		description = "Manage informations",
		url = "http://evercraft.fr/",
		authors = {"rexbut"},
		dependencies = {
		    @Dependency(id = "everapi", version = EverAPI.VERSION),
		    @Dependency(id = "spongeapi", version = EverAPI.SPONGEAPI_VERSION)
		})
public class EverInformations extends EPlugin<EverInformations> {
	private EIConfig configs;
	
	private EIMessage messages;	
	
	private ManagerAutoMessage automessages;
	private ManagerConnection connection;
	private ManagerNewbie newbie;
	
	private ManagerScoreBoard scoreboard;
	private ManagerNameTag nametag;
	private ManagerTabList tablist;
	private ManagerHealthMob healthmob;
	
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
		this.healthmob = new ManagerHealthMob(this);
	}
	
	@Override
	protected void onCompleteEnable() {
		EICommand command = new EICommand(this);
		command.add(new EIReload(this, command));
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
		this.healthmob.reload();
	}
	
	@Override
	protected void onStopServer() {
		if (this.healthmob != null) this.healthmob.reset();
	}
	
	@Override
	protected void onDisable() {
		if (this.automessages != null) this.automessages.stop();
		if (this.connection != null) this.connection.stop();
		if (this.newbie != null) this.newbie.stop();
		if (this.scoreboard != null) this.scoreboard.stop();
		if (this.nametag != null) this.nametag.stop();
		if (this.tablist != null) this.tablist.stop();
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
	
	public EIPermissions[] getPermissions() {
		return EIPermissions.values();
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

	public ManagerHealthMob getHealthMob() {
		return this.healthmob;
	}
}
