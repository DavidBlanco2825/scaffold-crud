package co.com.bancolombia.dynamodb.helper;

import co.com.bancolombia.dynamodb.DynamoDBTemplateAdapter;
import co.com.bancolombia.dynamodb.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<UserEntity> customerTable;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table("table_name", TableSchema.fromBean(UserEntity.class)))
                .thenReturn(customerTable);

        userEntity = new UserEntity();
        userEntity.setEmail("test@test.com");
        userEntity.setName("Jhon Doe");
        userEntity.setAge(25);
        userEntity.setStatus("ACTIVE");
    }

    @Test
    void modelEntityPropertiesMustNotBeNull() {
        UserEntity userEntityUnderTest = new UserEntity("test@test.com", "Jhon Doe", 25, "ACTIVE");

        assertNotNull(userEntityUnderTest.getEmail());
        assertNotNull(userEntityUnderTest.getName());
        assertNotNull(userEntityUnderTest.getAge());
        assertNotNull(userEntityUnderTest.getStatus());
    }

    @Test
    void testSave() {
        when(customerTable.putItem(userEntity)).thenReturn(CompletableFuture.runAsync(()->{}));
        when(mapper.map(userEntity, UserEntity.class)).thenReturn(userEntity);

        DynamoDBTemplateAdapter dynamoDBTemplateAdapter =
                new DynamoDBTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(dynamoDBTemplateAdapter.save(userEntity))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetById() {
        String id = "id";

        when(customerTable.getItem(
                Key.builder().partitionValue(AttributeValue.builder().s(id).build()).build()))
                .thenReturn(CompletableFuture.completedFuture(userEntity));
        when(mapper.map(userEntity, Object.class)).thenReturn("value");

        DynamoDBTemplateAdapter dynamoDBTemplateAdapter =
                new DynamoDBTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(dynamoDBTemplateAdapter.getById("id"))
                .expectNext("value")
                .verifyComplete();
    }

    @Test
    void testDelete() {
        when(mapper.map(userEntity, UserEntity.class)).thenReturn(userEntity);
        when(mapper.map(userEntity, Object.class)).thenReturn("value");

        when(customerTable.deleteItem(userEntity))
                .thenReturn(CompletableFuture.completedFuture(userEntity));

        DynamoDBTemplateAdapter dynamoDBTemplateAdapter =
                new DynamoDBTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(dynamoDBTemplateAdapter.delete(userEntity))
                .expectNext("value")
                .verifyComplete();
    }
}