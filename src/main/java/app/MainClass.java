package app;

import org.apache.logging.log4j.core.config.Configurator;
import bingimg.job.ImgJob;

import java.io.File;

public class MainClass {

	public static void main(String[] args) {
		Configurator.initialize("log4j2.xml",
				System.getProperty("user.dir") + File.separator + "log4j2.xml");
		TaskScheduler scheduler = new TaskScheduler();
		scheduler.addJob(ImgJob.class, "getBingImg","DEFAULT",
				"getBingImgTrigger", ImgJob.triggerExpression());
		scheduler.startTask();
	}
}
