package com.tutkowski.thevoice.bot.tasks;

import com.google.inject.Inject;
import com.tutkowski.thevoice.bot.Bot;
import com.tutkowski.thevoice.clients.leetcode.LeetCode;
import com.tutkowski.thevoice.clients.leetcode.LeetCodeQuestion;

public class PostLeetCodeDailyQuestionTask implements ScheduledTask {
    private final LeetCode leetCode;

    @Inject
    public PostLeetCodeDailyQuestionTask(LeetCode leetCode) {
        this.leetCode = leetCode;
    }

    @Override
    public long getInitialDelaySeconds() {
        return 0;
    }

    @Override
    public long getIntervalSeconds() {
        return 24 * 60 * 60;
    }

    @Override
    public Runnable getTask(Bot bot) {
        return () -> {
            try {
                LeetCodeQuestion question = this.leetCode.getDailyQuestion();
                String message = String.format("Daily LeetCode Question: [%s (%s)](%s)", question.title(), question.difficulty(), question.url());
                bot.createMessage("leetcode", message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
