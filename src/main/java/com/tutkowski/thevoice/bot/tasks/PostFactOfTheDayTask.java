package com.tutkowski.thevoice.bot.tasks;

import com.google.inject.Inject;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.clients.chatgpt.ChatGPT;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class PostFactOfTheDayTask implements ScheduledTask {
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
                String selfId = bot.getJda().getSelfUser().getId();
                TextChannel channel = bot.getJda().getGuilds().getFirst().getTextChannelsByName(this.channelName, true).getFirst();
                channel.getIterableHistory().takeAsync(700).thenAccept(messages -> {
                    List<String> recentFacts = messages.stream()
                            .filter(message -> message.getAuthor().getId().equals(selfId))
                            .map(Message::getContentDisplay)
                            .toList();

                    var fact = this.chatGPT.prompt(getPrompt(recentFacts));
                    bot.createMessage(this.channelName, fact.text());
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
