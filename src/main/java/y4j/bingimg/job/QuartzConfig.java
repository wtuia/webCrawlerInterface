package y4j.bingimg.job;


import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
	
	@Value("${cron}")
	private String cron;
	@Bean
	public JobDetail imgJob(){
		return JobBuilder.newJob(ImgJob.class)
				.withIdentity("pullImgTask")
				.storeDurably()
				.build();
	}
	@Bean
	public Trigger printTimeJobTrigger() {
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
		return TriggerBuilder.newTrigger()
				.forJob(imgJob())
				.withIdentity("pullImgTrigger")
				.withSchedule(cronScheduleBuilder)
				.build();
	}
}

