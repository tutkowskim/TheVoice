package com.tutkowski.thevoice.bot.tasks;

import com.google.inject.Inject;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.clients.chatgpt.ChatGPT;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PostFactOfTheDayTask implements ScheduledTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostFactOfTheDayTask.class);

    private final ChatGPT chatGPT;

    private final String channelName = "interesting-fact-of-the-day";

    @Inject
    public PostFactOfTheDayTask(ChatGPT chatGPT) {
        this.chatGPT = chatGPT;
    }

    @Override
    public String getCronSchedule() {
        return "0 17 * * *";
    }

    @Override
    public Runnable getTask(Bot bot) {
        return () -> {
            try {
                LOGGER.info("Fetching recent facts for channel #{}", this.channelName);
                String selfId = bot.getJda().getSelfUser().getId();
                TextChannel channel = bot.getJda().getGuilds().getFirst().getTextChannelsByName(this.channelName, true).getFirst();
                channel.getIterableHistory().takeAsync(700).thenAccept(messages -> {
                    List<String> recentFacts = messages.stream()
                            .filter(message -> message.getAuthor().getId().equals(selfId))
                            .map(Message::getContentDisplay)
                            .toList();

                    LOGGER.info("Generating fact of the day with {} recent facts", recentFacts.size());
                    var fact = this.chatGPT.prompt(getPrompt(recentFacts));
                    bot.createMessage(this.channelName, fact.text());
                    LOGGER.info("Posted fact of the day");
                });
            } catch (Exception e) {
                LOGGER.error("Failed to post fact of the day", e);
            }
        };
    }

    private String getPrompt(List<String> recentFacts) {
        return  "You are a blogger who posts interesting facts of the day for your user base.\n"
                + "Could you give me an interesting random fact that you haven't recently said?\n"
                + "Important: Only response with the fact and nothing else.\n"
                + "The following facts you have posted recently and should not repeat:\n"
                + String.join(System.lineSeparator(), recentFacts);
    }
}
