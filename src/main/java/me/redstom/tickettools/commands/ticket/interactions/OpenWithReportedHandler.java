package me.redstom.tickettools.commands.ticket.interactions;

import java.awt.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import me.redstom.tickettools.commands.ticket.Ticket;
import me.redstom.tickettools.commands.ticket.TicketManager;
import me.redstom.tickettools.commands.ticket.TicketOpeningReason;
import me.redstom.tickettools.utils.exceptions.TicketException;
import me.redstom.tickettools.utils.interactions.InteractionAction;
import me.redstom.tickettools.utils.messages.Embeds;
import me.redstom.tickettools.utils.messages.builder.MessageFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenWithReportedHandler implements InteractionAction<ButtonInteractionEvent> {

    private final TicketManager manager;
    private final JDA jda;
    private final Embeds embeds;

    @Override
    public void run(ButtonInteractionEvent event) throws TicketException, IOException {

        String userId = event.getButton().getId().split(";")[1];
        User user = jda.retrieveUserById(userId).complete();

        if (manager.exists(user)) {
            embeds.errorMessage("Un ticket avec cet utilisateur existe déjà !")
                    .reply(event)
                    .queue();

            return;
        }

        Ticket ticket = manager.create(user);
        ticket.forceOpening(event.getUser(), new TicketOpeningReason.Simple("Discussion suite à un signalement"));

        MessageFactory.create()
                .addEmbeds(new EmbedBuilder()
                        .setColor(Color.CYAN)
                        .setTitle("Ticket de signalement")
                        .setDescription("Ce ticket a été ouvert suite à un signalement de %s (%s)"
                                .formatted(
                                        event.getUser().getAsMention(),
                                        event.getChannel().getAsMention())))
                .send(ticket.getTo())
                .complete()
                .pin()
                .queue();

        event.getMessage()
                .editMessageComponents(ActionRow.of(Button.of(
                        ButtonStyle.LINK, ticket.getTo().getJumpUrl(), "Accéder au ticket de l'utilisateur signalé")))
                .queue();

        embeds.successMessage("Le ticket a été ouvert avec succès !\n:arrow_right: %s"
                        .formatted(ticket.getTo().getAsMention()))
                .reply(event)
                .queue();
    }
}
