package fr.evercraft.everinformations.scoreboard;

import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.scoreboard.config.ConfigBelowName;
import fr.evercraft.everinformations.scoreboard.config.ConfigList;
import fr.evercraft.everinformations.scoreboard.config.ConfigSidebar;
import fr.evercraft.everinformations.scoreboard.objective.BelowNameObjective;
import fr.evercraft.everinformations.scoreboard.objective.ListObjective;
import fr.evercraft.everinformations.scoreboard.objective.SidebarObjective;

public class ManagerScoreBoard {
	private final EverInformations plugin;
	
	private final ScoreBoard<BelowNameObjective> below_name;
	private final ScoreBoard<ListObjective> list;
	private final ScoreBoard<SidebarObjective> sidebar;
	
	public ManagerScoreBoard(final EverInformations plugin) {
		this.plugin = plugin;
		
		this.below_name = new ScoreBoard<BelowNameObjective>(this.plugin, new ConfigBelowName(this.plugin), DisplaySlots.BELOW_NAME);
		this.list = new ScoreBoard<ListObjective>(this.plugin, new ConfigList(this.plugin), DisplaySlots.LIST);
		this.sidebar = new ScoreBoard<SidebarObjective>(this.plugin, new ConfigSidebar(this.plugin), DisplaySlots.SIDEBAR);
	}
	
	public void reload() {
		this.below_name.reload();
		this.list.reload();
		this.sidebar.reload();
	}
	
	public void start() {
		this.below_name.start();
		this.list.start();
		this.sidebar.start();
	}

	public void stop() {
		this.below_name.stop();
		this.list.stop();
		this.sidebar.stop();
	}

	public void addPlayer(EPlayer player) {
		this.below_name.addPlayer(player);
		this.list.addPlayer(player);
		this.sidebar.addPlayer(player);
	}

	public void removePlayer(EPlayer player) {
		this.below_name.removePlayer(player);
		this.list.removePlayer(player);
		this.sidebar.removePlayer(player);
	}

	public void eventScoreBoard(EPlayer player, DisplaySlot display, String berfore_identifier) {
		if(display.equals(this.sidebar.getDisplaySlot())) {
			if(!berfore_identifier.equalsIgnoreCase(ScoreBoard.SIDEBAR_IDENTIFIER)) {
				this.sidebar.addPlayer(player);
			}
		} else if(display.equals(this.below_name.getDisplaySlot())) {
			if(!berfore_identifier.equalsIgnoreCase(ScoreBoard.BELOW_NAME_IDENTIFIER)) {
				this.below_name.addPlayer(player);
			}
		} else if(display.equals(this.list.getDisplaySlot())) {
			if(!berfore_identifier.equalsIgnoreCase(ScoreBoard.LIST_IDENTIFIER)) {
				this.list.addPlayer(player);
			}
		}
	}
}
