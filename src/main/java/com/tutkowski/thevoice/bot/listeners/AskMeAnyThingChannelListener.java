package com.tutkowski.thevoice.bot.listeners;

import com.google.inject.Inject;
import com.tutkowski.thevoice.clients.chatgpt.ChatGPT;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AskMeAnyThingChannelListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AskMeAnyThingChannelListener.class);

    private final ChatGPT chatGPT;

    @Inject
    public AskMeAnyThingChannelListener(ChatGPT chatGPT) {
        this.chatGPT = chatGPT;
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
        List<Message.Attachment> attachments = event.getMessage().getAttachments();

        LOGGER.info("Ask-me-anything message from {} with {} attachments", event.getAuthor().getName(), attachments.size());
        event.getChannel().sendTyping().queue(); // show typing indicator

        try {
            var reply = this.chatGPT.prompt(userMessage, attachments);

            String text = reply.text().trim();
            boolean hasText = !text.isEmpty();
            if (hasText) {
                event.getChannel().sendMessage(text).queue();
            }

            if (!reply.images().isEmpty()) {
                List<FileUpload> uploads = new ArrayList<>();
                int idx = 1;
                for (byte[] img : reply.images()) {
                    uploads.add(FileUpload.fromData(img, "chatgpt-image-" + idx + ".png"));
                    idx++;
                }

                MessageCreateAction action = event.getChannel().sendFiles(uploads);
                action.queue();
            }
            LOGGER.info("Ask-me-anything response sent: text={}, images={}", hasText, reply.images().size());
        } catch (Exception e) {
            LOGGER.error("Failed to respond to ask-me-anything message", e);
            event.getChannel().sendMessage("⚠️ Error talking to ChatGPT.").queue();
        }
    }
}
