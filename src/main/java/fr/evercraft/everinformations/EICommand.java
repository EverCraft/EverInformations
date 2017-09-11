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

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everinformations.EIMessage.EIMessages;

public class EICommand extends EParentCommand<EverInformations> {
	
	public EICommand(final EverInformations plugin) {
        super(plugin, "everinformations", "everinfos", "everinfo");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EIPermissions.EVERINFORMATIONS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EIMessages.DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EIPermissions.HELP.get());
	}
}
