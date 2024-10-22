package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<User, String, UserEntity> implements UserRepository {

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, User.class), "user_table");
    }

    @Override
    public Flux<User> findAll() {
        return this.scan()
                .flatMapMany(Flux::fromIterable);
    }

    // maybe we can change the name so that it is not necessary to override the method
    @Override
    public Mono<User> findById(String email) {
        return this.getById(email);
    }

    // check if it would be better to just define the delete(User user) in UserRepository
    @Override
    public Mono<Void> deleteById(String email) {
        return this.getById(email).flatMap(user -> this.delete(user).then());
    }
}
