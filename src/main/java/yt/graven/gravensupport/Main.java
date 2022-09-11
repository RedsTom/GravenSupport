package yt.graven.gravensupport;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(BotConfig.class);

        Main main = context.getBean(Main.class);
        main.run();
    }


    private final EventReceiver eventReceiver;
    private final YamlConfiguration config;
    private final JDA client;

    public Main(EventReceiver eventReceiver, YamlConfiguration config, JDA client) {
        this.eventReceiver = eventReceiver;
        this.config = config;
        this.client = client;
    }

    private void run() {
        this.client.getPresence().setPresence(
            Activity.listening(config.getString("config.prefix")
                + "new | Ouvrez un ticket avec la modération"),
            false
        );

        this.client.addEventListener(eventReceiver);
    }
}
