package BinanceDataBot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Timer implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        EventManager eventManager = (EventManager) arg0.getJobDetail().getJobDataMap().get("eventManager");
        String tag = (String) arg0.getJobDetail().getJobDataMap().get("tag");
        try {
            eventManager.publish(new ScheduleEvent(tag));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}