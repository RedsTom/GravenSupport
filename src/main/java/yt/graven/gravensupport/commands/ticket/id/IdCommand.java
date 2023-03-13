package yt.graven.gravensupport.commands.ticket.id;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.springframework.stereotype.Component;
import yt.graven.gravensupport.commands.ticket.Ticket;
import yt.graven.gravensupport.commands.ticket.TicketManager;
import yt.graven.gravensupport.utils.commands.ICommand;
import yt.graven.gravensupport.utils.exceptions.CommandCancelledException;
import yt.graven.gravensupport.utils.exceptions.TicketException;
import yt.graven.gravensupport.utils.messages.Embeds;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IdCommand implements ICommand {

    private final YamlConfiguration config;
    private final TicketManager ticketManager;
    private final Embeds embeds;

    @Override
    public String[] getNames() {
        return new String[] { "id" };
    }

    @Override
    public String getDescription() {
        return "Affiche l'ID du destinataire du ticket.";
    }

    @Override
    public boolean isShown() {
        return false;
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) throws TicketException, IOException, CommandCancelledException {

        if (event.getChannelType() == ChannelType.PRIVATE) {
            throw new CommandCancelledException();
        }

        if (!event.getGuild().getId().equals(config.getString("config.ticket_guild.guild_id"))) {
            throw new CommandCancelledException();
        }

        TextChannel textChannel = event.getTextChannel();
        if (!Objects.equals(textChannel.getParentCategoryId(), config.getString("config.ticket_guild.tickets_category"))) {
            embeds.errorMessage("Cette commande doit être exécutée dans un ticket !")
                .sendMessage(event.getChannel())
                .queue();
            return;
        }

        Optional<Ticket> ticket = ticketManager.get(MiscUtil.parseLong(((TextChannel) event.getChannel()).getTopic()));
        if (ticket.isEmpty()) {
            embeds.errorMessage("Impossible de trouver le ticket associé à ce salon !")
                .sendMessage(event.getChannel())
                .queue();
            return;
        }

        event.getChannel().sendMessage(ticket.get().getFrom().getId()).queue();
    }
}
