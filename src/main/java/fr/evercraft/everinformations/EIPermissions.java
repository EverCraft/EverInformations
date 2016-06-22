package fr.evercraft.everinformations;

import org.spongepowered.api.command.CommandSource;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EnumPermission;

public enum EIPermissions implements EnumPermission {
	EVERINFORMATIONS("command"),
	
	HELP("help"),
	RELOAD("reload");
	
	private final static String prefix = "everinformations";
	
	private final String permission;
    
    private EIPermissions(final String permission) {   	
    	Preconditions.checkNotNull(permission, "La permission '" + this.name() + "' n'est pas définit");
    	
    	this.permission = permission;
    }

    public String get() {
		return EIPermissions.prefix + "." + this.permission;
	}
    
    public boolean has(CommandSource player) {
    	return player.hasPermission(this.get());
    }
}
