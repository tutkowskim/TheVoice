package com.tutkowski.thevoice.bot.tasks;

import com.google.inject.Inject;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.clients.gemini.Gemini;

public class PostFactOfTheDayTask implements ScheduledTask {
    private final Gemini gemini;

    private final String PROMPT =  "Could you give me an interesting random fact that you haven't recently said?\n"
            + "Important: Only response with the fact and nothing else.";

    @Inject
    public PostFactOfTheDayTask(Gemini gemini) {
        this.gemini = gemini;
    }

    @Override
    public String getCronSchedule() {
        return "0 18 * * *";
    }

    @Override
    public Runnable getTask(Bot bot) {
        return () -> {
            try {
                String fact = this.gemini.prompt(PROMPT);
                bot.createMessage("interesting-fact-of-the-day", fact);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
