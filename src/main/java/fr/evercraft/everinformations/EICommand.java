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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.services.pagination.ESubCommand;
import fr.evercraft.everinformations.EIMessage.EIMessages;

public class EICommand extends ECommand<EverInformations> {
	
	public EICommand(final EverInformations plugin) {
		super(plugin, "everinformations", "everinfo");
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
	public Text help(final CommandSource source) {
		boolean help = source.hasPermission(EIPermissions.HELP.get());
		boolean reload = source.hasPermission(EIPermissions.RELOAD.get());

		Builder build;
		if(help || reload){
			build = Text.builder("/" + this.getName() + " <");
			if(help){
				build = build.append(Text.builder("help").onClick(TextActions.suggestCommand("/" + this.getName() + " help")).build());
				if(reload){
					build = build.append(Text.builder("|").build());
				}
			}
			if(reload){
				build = build.append(Text.builder("reload").onClick(TextActions.suggestCommand("/" + this.getName() + " reload")).build());
			}
			build = build.append(Text.builder(">").build());
		} else {
			build = Text.builder("/" + this.getName()).onClick(TextActions.suggestCommand("/" + this.getName()));
		}
		return build.color(TextColors.RED).build();
	}
	
	public Text helpReload(final CommandSource source) {
		return Text.builder("/" + this.getName() + " reload")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " reload"))
					.color(TextColors.RED)
					.build();
	}

	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1) {
			if(source.hasPermission(EIPermissions.RELOAD.get())) {
				suggests.add("reload");
			}
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		boolean resultat = false;
		if(args.size() == 0 || args.get(0).equalsIgnoreCase("help")) {
			if(source.hasPermission(EIPermissions.HELP.get())) {
				resultat = commandHelp(source);
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else if(args.size() == 1) {
			if(args.get(0).equalsIgnoreCase("reload")) {
				if(source.hasPermission(EIPermissions.RELOAD.get())) {
					resultat = commandReload(source);
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			} else {
				source.sendMessage(help(source));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean commandHelp(final CommandSource source) {
		LinkedHashMap<String, ESubCommand> commands = new LinkedHashMap<String, ESubCommand>();
		if(source.hasPermission(EIPermissions.RELOAD.get())) {
			commands.put(this.getName() + " reload", new ESubCommand(this.helpReload(source), EAMessages.RELOAD_DESCRIPTION.getText()));
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().helpSubCommand(commands, source, this.plugin);
		return true;
	}

	private boolean commandReload(CommandSource player) {
		this.plugin.reload();
		player.sendMessage(EIMessages.PREFIX.getText().concat(EAMessages.RELOAD_COMMAND.getText()));
		return true;
	}
}
