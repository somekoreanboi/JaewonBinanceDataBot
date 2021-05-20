package BinanceDataBot;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerManager {

    private final EventManager eventManager;
    private final Scheduler scheduler;

    public SchedulerManager(EventManager eventManager) throws SchedulerException {
        this.eventManager = eventManager;
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
    }

    public void periodicCallBack(int intervalMillis, String tag) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger()
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(intervalMillis)
                        .repeatForever())
                .build();

        JobDetail timer = JobBuilder.newJob(Timer.class)
                .build();
        timer.getJobDataMap().put("eventManager", this.eventManager);
        timer.getJobDataMap().put("tag", tag);
        scheduler.scheduleJob(timer, trigger);
        scheduler.start();
    }
}
