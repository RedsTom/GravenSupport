package me.redstom.tickettools.utils.messages.builder.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

public class TicketActionRow {

    private final List<ItemComponent> components = new ArrayList<>();

    public TicketActionRow() {}

    public TicketActionRow(ItemComponent... components) {
        this.components.addAll(List.of(components));
    }

    public TicketActionRow addButton(String id, UnaryOperator<TicketButton> applier) {
        TicketButton button = new TicketButton(id);
        applier.apply(button);

        components.add(button.build());
        return this;
    }

    public TicketActionRow addButton(UnaryOperator<TicketButton> applier) {
        TicketButton button = new TicketButton();
        applier.apply(button);

        components.add(button.build());
        return this;
    }

    public TicketActionRow addDeleteButton() {
        return addButton(button -> button.setId("delete").setEmoji(Emoji.fromUnicode("🗑️")));
    }

    public TicketActionRow addSelectMenu(String id, UnaryOperator<TicketSelectMenu> applier) {
        TicketSelectMenu menu = new TicketSelectMenu(id);
        applier.apply(menu);

        components.add(menu.build());
        return this;
    }

    public Optional<ActionRow> build() {
        if (components.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ActionRow.of(components));
    }
}
