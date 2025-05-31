package com.tutkowski.thevoice.bot.tasks;

import com.tutkowski.thevoice.bot.Bot;

public interface ScheduledTask {
    String getCronSchedule();
    Runnable getTask(Bot bot);
}