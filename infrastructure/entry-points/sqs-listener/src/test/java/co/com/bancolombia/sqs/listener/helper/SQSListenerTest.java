package co.com.bancolombia.sqs.listener.helper;

import co.com.bancolombia.sqs.listener.SQSProcessor;
import co.com.bancolombia.sqs.listener.config.SQSProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SQSListenerTest {

    @Mock
    private SqsAsyncClient asyncClient;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        Message message = Message.builder().body("message").build();
        DeleteMessageResponse deleteMessageResponse = DeleteMessageResponse.builder().build();
        ReceiveMessageResponse messageResponse = ReceiveMessageResponse.builder().messages(message).build();

        when(asyncClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(CompletableFuture.completedFuture(messageResponse));
        when(asyncClient.deleteMessage(any(DeleteMessageRequest.class))).thenReturn(CompletableFuture.completedFuture(deleteMessageResponse));

    }

    @Test
    void listenerTest() {
        SQSProperties sqsProperties = new SQSProperties();
        sqsProperties.setNumberOfThreads(1);
        sqsProperties.setRegion("Region");

        SQSListener sqsListener = SQSListener.builder()
            .client(asyncClient)
            .properties(sqsProperties)
            .processor(new SQSProcessor())
            .operation("operation")
            .build();

        Flux<Void> flow = ReflectionTestUtils.invokeMethod(sqsListener, "listen");

        StepVerifier.create(flow)
            .verifyComplete();

    }
}