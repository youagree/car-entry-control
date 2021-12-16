package ru.unit_techno.car_entry_control.ws_test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.unit_techno.car_entry_control.dto.response.BarrierUnavailable;
import ru.unit_techno.car_entry_control.dto.response.NewRfidLabelMessage;
import ru.unit_techno.car_entry_control.service.WSNotificationService;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.unit_techno.car_entry_control.util.Constant.RFID_UNKNOWN_EXCEPTION_MESSAGE;

public class WebSocketTests extends BaseTestClass {

    @Value("${local.server.port}")
    private int port;
    @Autowired
    private WSNotificationService notificationService;

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @AfterEach
    public void clearQueue() {
        blockingQueue.clear();
    }

    @Test
    @DisplayName("Проверка, что из вебсокета получено оповещение о том, что нужно активировать новую метку")
    public void checkNewRfidLabelPositive() throws Exception {
        StompSession session = stompClient
                .connect("ws://localhost:" + port + "/gs-guide-websocket", new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);
        session.subscribe("/topic/newrfidlabel", new DefaultStompFrameHandler());

        notificationService.sendNotActive(1234L, RFID_UNKNOWN_EXCEPTION_MESSAGE);

        String receivedMessage = blockingQueue.poll(1, SECONDS);
        NewRfidLabelMessage result = objectMapper.readValue(receivedMessage, NewRfidLabelMessage.class);
        Assertions.assertEquals(result.getRfidLabelValue(), 1234L);
        Assertions.assertEquals(result.getMessage(), "Произошла неизвестная ошибка");
    }

    @SneakyThrows
    @Test
    @DisplayName("Проверка, что из вебсокета получено оповещение о том, что какой то из сервисов отказал")
    public void checkActiveButSomethingUnavailablePos() {
        StompSession session = stompClient
                .connect("ws://localhost:" + port + "/gs-guide-websocket", new StompSessionHandlerAdapter() {
                })
                .get(3, SECONDS);
        session.subscribe("/topic/newrfidlabel", new DefaultStompFrameHandler());

        notificationService.sendActiveButSomethingUnavailable("1234", 1234L, RFID_UNKNOWN_EXCEPTION_MESSAGE);

        String receivedMessage = blockingQueue.poll(3, SECONDS);
        BarrierUnavailable result = objectMapper.readValue(receivedMessage, BarrierUnavailable.class);
        Assertions.assertEquals(result.getBarrierName(), "1234");
        Assertions.assertEquals(result.getDeviceId(), 1234L);
        Assertions.assertEquals(result.getNotificationMessage(), "Произошла неизвестная ошибка");
    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }

}
