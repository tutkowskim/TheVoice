package com.tutkowski.thevoice.bot.tasks;

import com.google.inject.Inject;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.clients.gemini.Gemini;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class PostFactOfTheDayTask implements ScheduledTask {
    private final Gemini gemini;

    private final String channelName = "interesting-fact-of-the-day";

    private final String PROMPT =  "Could you give me an interesting random fact that you haven't recently said?\n"
            + "Important: Only response with the fact and nothing else.";

    @Inject
    public PostFactOfTheDayTask(Gemini gemini) {
        this.gemini = gemini;
    }

    @Override
    public String getCronSchedule() {
        return "0 17 * * *";
    }

    @Override
    public Runnable getTask(Bot bot) {
        return () -> {
            try {
                String selfId = bot.getJda().getSelfUser().getId();
                TextChannel channel = bot.getJda().getGuilds().getFirst().getTextChannelsByName(this.channelName, true).getFirst();
                channel.getIterableHistory().takeAsync(700).thenAccept(messages -> {
                    List<String> recentFacts = messages.stream()
                            .filter(message -> message.getAuthor().getId().equals(selfId))
                            .map(Message::getContentDisplay)
                            .toList();

                    String fact = this.gemini.prompt(getPrompt(recentFacts));
                    bot.createMessage(this.channelName, fact);
                });
            } catch (Exception e) {
                e.printStackTrace();
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
