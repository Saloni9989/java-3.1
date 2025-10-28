public class MyApplication {
    private MessageService service;

    public MyApplication(MessageService service) {
        this.service = service;
    }

    public void processMessage(String message) {
        service.sendMessage(message);
    }
}
