package dv.springframework.spring5jmsexample.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dv.springframework.spring5jmsexample.config.JmsConfig;
import dv.springframework.spring5jmsexample.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage(){
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);

    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        System.out.println("I'm sending a message");

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();

        Message receivedMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, session -> {
            Message helloMessage;
            try {
                helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                helloMessage.setStringProperty("_type", "dv.springframework.spring5jmsexample.model.HelloWorldMessage");
                System.out.println("Sending hello!");
                return helloMessage;
            } catch (JsonProcessingException e) {
                throw new JMSException("boom");
            }

        });

        System.out.println(receivedMsg != null ? receivedMsg.getBody(String.class) : null);
    }
}
