package yt.graven.gravensupport.commands.ticket.create.interactions;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.springframework.stereotype.Component;
import yt.graven.gravensupport.commands.ticket.Ticket;
import yt.graven.gravensupport.commands.ticket.TicketManager;
import yt.graven.gravensupport.utils.interactions.IIInteractionAction;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EditMessageHandler implements IIInteractionAction<ButtonInteractionEvent> {

    private final TicketManager ticketManager;

    @Override
    public void run(ButtonInteractionEvent event) {
        Message embedMessage = event.getMessage();
        MessageEmbed baseEmbed = embedMessage.getEmbeds().get(0);

        Optional<Ticket> ticket = ticketManager.get(MiscUtil.parseLong(((TextChannel) event.getChannel()).getTopic()));
        if (ticket.isEmpty()) {
            event.deferReply(true)
                .addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Erreur")
                    .setDescription("Impossible de trouver le ticket associé à ce salon !")
                    .setFooter("")
                    .build())
                .queue();
            return;
        }

        long messageId = MiscUtil.parseLong(baseEmbed.getFields().get(baseEmbed.getFields().size() - 1).getValue());

        Message referingMessage = ticket.get().getFrom().openPrivateChannel().complete()
            .getHistoryAround(messageId, 50)
            .complete()
            .getMessageById(messageId);

        if (referingMessage == null) {
            event.deferReply(true)
                .addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Erreur")
                    .setDescription("Impossible de trouver le message cible associé à cet envoi !")
                    .build())
                .queue();
            return;
        }

        String originalMessageId = baseEmbed.getFields().get(0).getValue();
        originalMessageId = originalMessageId
            .replace(originalMessageId.replaceAll("\\[[0-9]+]", ""), "")
            .replaceAll("[\\[\\]]", "");

        Message originalMessage =
            ticket.get().getTo().getHistoryAround(originalMessageId, 50).complete().getMessageById(originalMessageId);

        if (originalMessage == null) {
            event.deferReply(true)
                .addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Erreur")
                    .setDescription("Impossible de trouver le message source associé à cet envoi !")
                    .build())
                .queue();
            return;
        }

        String before = referingMessage.getContentRaw();
        String after = originalMessage.getContentRaw().startsWith("'")
            ? originalMessage.getContentRaw().substring(1).trim()
            : originalMessage.getContentRaw().trim();

        if (before.equals(after)) {
            event.deferReply(true)
                .setContent("Aucune modification n'a été effectuée !")
                .queue();
            return;
        }

        referingMessage.editMessage(after)
            .queue();


        List<MessageEmbed> embedList = new ArrayList<>(event.getMessage().getEmbeds());
        embedList.add(new EmbedBuilder()
            .setTitle("Message édité")
            .addField("Avant : ", before, true)
            .addField("Après : ", after, true)
            .setTimestamp(Instant.now())
            .setColor(Color.ORANGE)
            .build());
        event.deferEdit()
            .setEmbeds(embedList)
            .queue();
    }
}
