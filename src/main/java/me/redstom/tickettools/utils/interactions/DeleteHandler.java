package me.redstom.tickettools.utils.interactions;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class DeleteHandler implements InteractionAction<ButtonInteractionEvent> {
    @Override
    public void run(ButtonInteractionEvent event) {
        try {
            event.deferEdit().queue();
            event.getMessage().delete().queue();
        } catch (Exception ignored) {
        }
    }
}
