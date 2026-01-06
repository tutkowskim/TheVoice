package com.tutkowski.thevoice.bot.tasks;

import com.google.inject.Inject;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.clients.leetcode.LeetCode;
import com.tutkowski.thevoice.clients.leetcode.LeetCodeQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostLeetCodeDailyQuestionTask implements ScheduledTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostLeetCodeDailyQuestionTask.class);

    private final LeetCode leetCode;

    @Inject
    public PostLeetCodeDailyQuestionTask(LeetCode leetCode) {
        this.leetCode = leetCode;
    }

    @Override
    public String getCronSchedule() {
        return "0 1 * * *";
    }

    @Override
    public Runnable getTask(Bot bot) {
        return () -> {
            try {
                LOGGER.info("Fetching LeetCode daily question");
                LeetCodeQuestion question = this.leetCode.getDailyQuestion();
                String message = String.format("Daily LeetCode Question: [%s (%s)](%s)", question.title(), question.difficulty(), question.url());
                bot.createMessage("leetcode", message);
                LOGGER.info("Posted LeetCode daily question: {} ({})", question.title(), question.difficulty());
            } catch (Exception e) {
                LOGGER.error("Failed to post LeetCode daily question", e);
            }
        };
    }
}
