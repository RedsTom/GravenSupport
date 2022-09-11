package yt.graven.gravensupport.commands.ping;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class PingManager {

    private final JDA client;

    public PingManager(JDA client) {
        this.client = client;
    }

    public MessageEmbed compute() {
        return new EmbedBuilder()
            .setTitle(":ping_pong: Pong !")
            .setColor(Color.green)
            .addField("↔️ Ping du Gateway :", String.format("""
                    **`%s`** ms
                """.trim(), client.getGatewayPing()), false)
            .addField("➡️ Ping de l'API :", String.format("""
                    **`%s`** ms
                """.trim(), client.getRestPing().complete()), false)
            .build();
    }

}
