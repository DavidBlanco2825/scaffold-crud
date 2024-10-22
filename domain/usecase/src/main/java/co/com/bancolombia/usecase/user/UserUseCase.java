package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.BadRequestException;
import co.com.bancolombia.model.exception.ResourceNotFoundException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> addUser(User user) {
        return userRepository.findById(user.getEmail())
                .flatMap(existingUser ->
                        Mono.<User>error(new BadRequestException("There is already a user created with that email."))
                )
                .switchIfEmpty(userRepository.save(user));
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> findUserByEmail(String email) {
        return userRepository.findById(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with email: " + email)))
                .flatMap(user -> {
                    if (user.getStatus().equals("INACTIVE")) {
                        return Mono.error(new BadRequestException("User status is Inactive"));
                    }
                    return Mono.just(user);
                });
    }

    public Mono<User> setActiveByEmail(String email) {
        return userRepository.findById(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with email: " + email)))
                .flatMap(user -> {
                    user.setStatus("ACTIVE");
                    return userRepository.save(user);
                });
    }

    public Mono<Void> setUsersInactiveFromMessageBody(String messageBody) {
        String[] emails = messageBody.split(",");

        return Flux.fromArray(emails)
                .map(String::trim)
                .flatMap(this::setInactiveByEmail)
                .then();
    }

    public Mono<User> setInactiveByEmail(String email) {
        return userRepository.findById(email)
                .switchIfEmpty(Mono.empty())
                .flatMap(user -> {
                    user.setStatus("INACTIVE");
                    return userRepository.save(user);
                });
    }

    public Mono<Void> deleteUser(String email) {
        return userRepository.findById(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with email: " + email)))
                .flatMap(user -> userRepository.deleteById(email))
                .then();
    }
}
