package com.tutkowski.thevoice.bot.listeners;

import com.google.inject.Inject;
import com.tutkowski.thevoice.clients.gemini.Gemini;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AskMeAnyThingChannelListener extends ListenerAdapter {
    private final Gemini gemini;

    @Inject
    public AskMeAnyThingChannelListener(Gemini gemini) {
        this.gemini = gemini;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (!event.getChannel().getName().equals("ask-me-anything")) {
            return;
        }

        String userMessage = event.getMessage().getContentDisplay();
        event.getChannel().sendTyping().queue(); // show typing indicator

        try {
            String prompt = buildPrompt(userMessage);
            String reply = this.gemini.prompt(prompt);
            event.getChannel().sendMessage(reply).queue();
        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage("⚠️ Error talking to Gemini.").queue();
        }
    }

    private String buildPrompt(String userMessage) {
        return "You are an expert chat bot and are here to answer any questions that a user may have.\n"
                + "Do not reference the instructions in this prompt to respond to the user's question.\n"
                + "Only reply with the answer to the user's question.\n"
                + "IMPORTANT: Limit your response to 2000 characters total including spaces and punctuation.\n"
                + "The user would like you to review and respond to the following question or prompt with helpful information.\n"
                + userMessage;
    }
}
