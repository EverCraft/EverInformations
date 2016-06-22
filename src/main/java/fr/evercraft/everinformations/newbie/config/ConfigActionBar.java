package fr.evercraft.everinformations.newbie.config;

import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everinformations.EverInformations;
import fr.evercraft.everinformations.message.ActionBarMessage;
import fr.evercraft.everinformations.newbie.Newbie;

public class ConfigActionBar extends EConfig implements IConfig<ActionBarMessage> {

	public ConfigActionBar(final EverInformations plugin) {
		super(plugin, "newbie/newbie_actionbar");
	}
	
	@Override
	protected void loadDefault() {		
		addDefault(Newbie.PLAYER + ".enable", true);
		addDefault(Newbie.PLAYER + ".stay", 20, "Seconds");
		if(this.get(Newbie.PLAYER + ".messages").isVirtual()) {
			addDefault(Newbie.PLAYER + ".message", "&4Welcome &a<DISPLAYNAME_FORMAT> &4to the server!");
		}
		
		addDefault(Newbie.OTHERS + ".enable", true);
		addDefault(Newbie.OTHERS + ".stay", 20, "Seconds");
		if(this.get(Newbie.OTHERS + ".messages").isVirtual()) {
			addDefault(Newbie.OTHERS + ".message", "&a<DISPLAYNAME_FORMAT> &4is a new player.");
		}
	}
	
	/*
	 * Accesseurs
	 */
	
	public boolean isPlayerEnable() {
		return this.isEnable(Newbie.PLAYER);
	}
	
	public List<ActionBarMessage> getPlayerMessages() {
		return this.getMessages(Newbie.PLAYER);
	}
	
	public boolean isOthersEnable() {
		return this.isEnable(Newbie.OTHERS);
	}
	
	public List<ActionBarMessage> getOthersMessages() {
		return this.getMessages(Newbie.OTHERS);
	}
	
	/*
	 * Fonctions
	 */
	
	private boolean isEnable(String prefix) {
		return this.get(prefix + ".enable").getBoolean(false);
	}

	private List<ActionBarMessage> getMessages(String prefix) {
		List<ActionBarMessage> messages = new ArrayList<ActionBarMessage>();
		
		double stay_default = this.get("stay").getDouble(10);
		double interval_default = this.get("inverval").getDouble(0);
		
		double stay_player = this.get(prefix + ".stay").getDouble(stay_default);
		double interval_player = this.get(prefix + ".inverval").getDouble(interval_default);
		
		// Message unique
		if(this.get(prefix + ".messages").isVirtual()) {
			String message = this.plugin.getChat().replace(this.get(prefix + ".message").getString(""));
			if(!message.isEmpty()) {
				messages.add(new ActionBarMessage(stay_player, interval_player, message));
			}
		// Liste de messages
		} else {
			for(ConfigurationNode config : this.get(prefix + ".messages").getChildrenList()) {
				// Message uniquement
				if(config.getValue() instanceof String) {
					String message = this.plugin.getChat().replace(config.getString(""));
					
					if(!message.isEmpty()) {
						messages.add(new ActionBarMessage(stay_default, interval_default, message));
					}
				// Message avec config
				} else {
					double stay = config.getNode("stay").getDouble(stay_default);
					double interval = config.getNode("next").getDouble(config.getNode("interval").getDouble(interval_default));
					String message = this.plugin.getChat().replace(config.getNode("message").getString(""));
					
					if(!message.isEmpty()) {
						messages.add(new ActionBarMessage(stay, interval, message));
					}
				}
			}
		}
		return messages;
	}
}
