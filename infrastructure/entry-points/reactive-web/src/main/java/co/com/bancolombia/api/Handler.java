package co.com.bancolombia.api;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private static final String EMAIL = "email";
    private final UserUseCase userUseCase;

    public Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(userUseCase::addUser)
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                );
    }

    public Mono<ServerResponse> findUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable(EMAIL);
        return userUseCase.findUserByEmail(email)
                .flatMap(user -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                );
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.getAllUsers(), User.class);
    }

    public Mono<ServerResponse> setActiveByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable(EMAIL);
        return userUseCase.setActiveByEmail(email)
                .flatMap(user -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                );
    }

    public Mono<ServerResponse> setInactiveByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable(EMAIL);
        return userUseCase.setInactiveByEmail(email)
                .flatMap(user -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                );
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable(EMAIL);

        return userUseCase.deleteUser(email)
                .then(ServerResponse.noContent().build());
    }
}
