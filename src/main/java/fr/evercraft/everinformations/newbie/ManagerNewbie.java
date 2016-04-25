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
package fr.evercraft.everinformations.newbie;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.newbie.actionbar.ActionBarOthersNewbie;
import fr.evercraft.everinformations.newbie.actionbar.ActionBarPlayerNewbie;
import fr.evercraft.everinformations.newbie.chat.ChatOthersNewbie;
import fr.evercraft.everinformations.newbie.chat.ChatPlayerNewbie;
import fr.evercraft.everinformations.newbie.title.TitleOthersNewbie;
import fr.evercraft.everinformations.newbie.title.TitlePlayerNewbie;

public class ManagerNewbie {
	private final EverInformations plugin;
	
	private final ChatPlayerNewbie chatPlayer;
	private final ChatOthersNewbie chatOthers;
	
	private final TitlePlayerNewbie titlePlayer;
	private final TitleOthersNewbie titleOthers;

	private final ActionBarPlayerNewbie actionbarPlayer;
	private final ActionBarOthersNewbie actionbarOthers;
	
	public ManagerNewbie(final EverInformations plugin) {
		this.plugin = plugin;
		
		this.chatPlayer = new ChatPlayerNewbie(this.plugin);
		this.chatOthers = new ChatOthersNewbie(this.plugin);
		
		this.titlePlayer = new TitlePlayerNewbie(this.plugin);
		this.titleOthers = new TitleOthersNewbie(this.plugin);
		
		this.actionbarPlayer = new ActionBarPlayerNewbie(this.plugin);
		this.actionbarOthers = new ActionBarOthersNewbie(this.plugin);
	}
	
	public void reload() {
		this.chatPlayer.reload();
		this.chatOthers.reload();
		
		this.titlePlayer.reload();
		this.titleOthers.reload();
		
		this.actionbarPlayer.reload();
		this.actionbarOthers.reload();
	}

	public void addPlayer(EPlayer player) {
		this.chatPlayer.addPlayer(player);
		this.chatOthers.addPlayer(player);
		
		this.titlePlayer.addPlayer(player);
		this.titleOthers.addPlayer(player);
		
		this.actionbarPlayer.addPlayer(player);
		this.actionbarOthers.addPlayer(player);
	}
	
	public void removePlayer(EPlayer player) {
		this.chatPlayer.removePlayer(player);
		this.chatOthers.removePlayer(player);
		
		this.titlePlayer.removePlayer(player);
		this.titleOthers.removePlayer(player);
		
		this.actionbarPlayer.removePlayer(player);
		this.actionbarOthers.removePlayer(player);
	}
}
