package app;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskScheduler {
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);
    private Map<String, Scheduler> TASK_MAP = new HashMap<>();
    private static StdSchedulerFactory  schedulerFactory = new StdSchedulerFactory();

    /**
     * 添加任务
     * @param taskClass 执行任务
     * @param taskName 任务名称
     * @param taskGroup 任务组名
     * @param triggerExpression 任务执行规则
     */
    public void addJob(Class<? extends Job> taskClass, String taskName, String taskGroup,
                       String triggerName,
                       String triggerExpression) {
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = JobBuilder.newJob(taskClass)
                        .withIdentity(taskName,taskGroup)
                        .build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                                    .withIdentity(triggerName, taskGroup)
                                    .withSchedule(CronScheduleBuilder.cronSchedule(triggerExpression))
                                    .build();
            Date date = scheduler.scheduleJob(job, trigger);
            logger.info("任务:{},首次安排于{}执行，并重复以下规则:{}",
					job.getKey(), date, trigger.getCronExpression());
            TASK_MAP.put(taskName, scheduler);
        } catch (SchedulerException e) {
            logger.error("安排任务{}失败:", taskGroup + taskName, e);
        }
    }

	public void startTask() {
        for (Map.Entry<String, Scheduler> entry:TASK_MAP.entrySet()){
            Scheduler task = entry.getValue();
            try {
                task.start();
            } catch (SchedulerException e) {
                logger.error("启动任务{}失败:" ,entry.getKey(),e);
            }
        }
    }

}
