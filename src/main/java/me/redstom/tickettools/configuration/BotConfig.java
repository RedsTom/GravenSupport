package me.redstom.tickettools.configuration;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Optional;
import me.redstom.tickettools.common.exception.BotStartupException;
import me.redstom.tickettools.common.exception.ImpossibleException;
import me.redstom.tickettools.configuration.exception.ConfigurationException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("yt.graven.gravensupport")
public class BotConfig {
    private static final String CONFIGURATION_FILE = "config.yml";
    private static final String DEFAULT_CONFIGURATION_FILE = "config.default.yml";
    private static final String CONFIGURATION_TOKEN_PROPERTY = "config.token";

    @Bean
    public YamlConfiguration getBotConfiguration() {
        Path configurationFile = Paths.get(CONFIGURATION_FILE);

        if (!Files.exists(configurationFile)) {
            try {
                Files.createFile(configurationFile);
                Path defaultConfigFile = getDefaultConfigFromInsideJar();
                String defaultConfigurationData =
                        String.join(System.lineSeparator(), Files.readAllLines(defaultConfigFile));
                Files.writeString(configurationFile, defaultConfigurationData);
            } catch (IOException exception) {
                throw new ConfigurationException("Unable to create default configuration file!", exception);
            }

            throw new ConfigurationException("Configuration file did not exist and has been created!");
        }

        return YamlConfiguration.loadConfiguration(configurationFile.toFile());
    }

    @Bean
    @SuppressWarnings("unused")
    public JDA getJDAInstance() {
        String token = this.getBotConfiguration().getString(CONFIGURATION_TOKEN_PROPERTY);
        if (token.isEmpty()) {
            throw new BotStartupException("No token provided!");
        }

        EnumSet<GatewayIntent> allIntentsBecauseWhyNot = EnumSet.allOf(GatewayIntent.class);
        return JDABuilder.create(allIntentsBecauseWhyNot).setToken(token).build();
    }

    private Path getDefaultConfigFromInsideJar() {
        try {
            URL resourceUrl = Optional.ofNullable(getClass().getClassLoader().getResource(DEFAULT_CONFIGURATION_FILE))
                    .orElseThrow(
                            () -> new ConfigurationException("Unable to retrieve default configuration file in JAR"));
            return Paths.get(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new ImpossibleException("a JAR file URL is a valid URI, wtf happened?");
        }
    }
}
