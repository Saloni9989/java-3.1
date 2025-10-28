import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MessageService messageService() {
        return new EmailService();
    }

    @Bean
    public MyApplication myApplication() {
        return new MyApplication(messageService());
    }
}
