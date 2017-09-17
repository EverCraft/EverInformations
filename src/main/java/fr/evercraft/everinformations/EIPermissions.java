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

import fr.evercraft.everapi.plugin.EnumPermission;
import fr.evercraft.everapi.plugin.file.EnumMessage;
import fr.evercraft.everinformations.EIMessage.EIMessages;

public enum EIPermissions implements EnumPermission {
	EVERINFORMATIONS("commands.execute", EIMessages.PERMISSIONS_COMMANDS_EXECUTE),
	HELP("commands.help", EIMessages.PERMISSIONS_COMMANDS_HELP),
	RELOAD("commands.reload", EIMessages.PERMISSIONS_COMMANDS_RELOAD);
	
	private static final String PREFIX = "everinformations";
	
	private final String permission;
	private final EnumMessage message;
	private final boolean value;
    
    private EIPermissions(final String permission, final EnumMessage message) {
    	this(permission, message, false);
    }
    
    private EIPermissions(final String permission, final EnumMessage message, final boolean value) {   	    	
    	this.permission = PREFIX + "." + permission;
    	this.message = message;
    	this.value = value;
    }

    @Override
    public String get() {
    	return this.permission;
	}

	@Override
	public boolean getDefault() {
		return this.value;
	}

	@Override
	public EnumMessage getMessage() {
		return this.message;
	}
}
