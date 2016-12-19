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

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.message.EMessageFormat;
import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EIMessage extends EMessage<EverInformations> {

	public EIMessage(final EverInformations plugin) {
		super(plugin, EIMessages.values());
	}
	
	public enum EIMessages implements EnumMessage {
		PREFIX("prefix", "[&4Ever&6&lInformations&f] "),
		
		DESCRIPTION("description", "Gestion des informations"),
		
		SCOREBOARD_EMPTY("scoreboard.empty", "&aAucun joueur", "&aNo player");
		
		private final String path;
	    private final EMessageFormat french;
	    private final EMessageFormat english;
	    private EMessageFormat message;
	    
	    private EIMessages(final String path, final String french) {   	
	    	this(path, 
	    		EMessageFormat.builder().chat(new EFormatString(french), true).build(), 
	    		EMessageFormat.builder().chat(new EFormatString(french), true).build());
	    }
	    
	    private EIMessages(final String path, final String french, final String english) {   	
	    	this(path, 
	    		EMessageFormat.builder().chat(new EFormatString(french), true).build(), 
	    		EMessageFormat.builder().chat(new EFormatString(english), true).build());
	    }
	    
	    private EIMessages(final String path, final EMessageFormat french) {   	
	    	this(path, french, french);
	    }
	    
	    private EIMessages(final String path, final EMessageFormat french, final EMessageFormat english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = path;	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french;
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public Object getFrench() {
			return this.french;
		}

		public Object getEnglish() {
			return this.english;
		}
		
		public EMessageFormat getMessage() {
			return this.message;
		}
		
		public void set(EMessageFormat message) {
			this.message = message;
		}
	}
}
