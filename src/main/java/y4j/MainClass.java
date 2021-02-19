package y4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import y4j.listener.CloseListener;

@SpringBootApplication
@PropertySource(value={"file:application.properties"})
public class MainClass {

	public static void main(String[] args) {
		SpringApplication sa = new SpringApplication(MainClass.class);
		sa.addListeners(new CloseListener());
		sa.run(args);
	}
	
}
