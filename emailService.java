public interface MessageService {
    void sendMessage(String message);
}

public class EmailService implements MessageService {
    @Override
    public void sendMessage(String message) {
        System.out.println("Email Message Sent: " + message);
    }
}
