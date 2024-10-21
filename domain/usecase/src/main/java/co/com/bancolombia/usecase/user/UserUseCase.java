package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.BadRequestException;
import co.com.bancolombia.model.exception.ResourceNotFoundException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> addUser(User user) {
        log.info("Attempting to add new user: {}", user.getEmail());

        return userRepository.findById(user.getEmail())
                .flatMap(existingUser -> {
                    log.warn("User email {} is already registered", user.getEmail());
                    return Mono.<User>error(new BadRequestException("There is already a user created with that email."));
                })
                .switchIfEmpty(userRepository.save(user)
                        .doOnSuccess(savedUser -> log.info("User added successfully: {}", savedUser.getEmail()))
                        .doOnError(error -> log.error("Failed to add user: {}", error.getMessage()))
                );
    }

    public Flux<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .doOnComplete(() -> log.info("Fetched all users successfully"))
                .doOnError(error -> log.error("Failed to fetch users: {}", error.getMessage()));
    }

    public Mono<User> findUserByEmail(String email) {
        log.info("Searching for user with email: {}", email);
        return userRepository.findById(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with email: " + email)))
                .flatMap(user -> {
                    if (user.getStatus().equals("INACTIVE")) {
                        log.warn("User with email {} is inactive", email);
                        return Mono.error(new BadRequestException("User status is Inactive"));
                    }
                    log.info("User with email {} found and active", email);
                    return Mono.just(user);
                })
                .doOnError(error -> log.error("Error finding user with email {}: {}", email, error.getMessage()));
    }

    public Mono<User> setActiveByEmail(String email) {
        log.info("Setting user with email {} to ACTIVE", email);
        return userRepository.findById(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with email: " + email)))
                .flatMap(user -> {
                    user.setStatus("ACTIVE");
                    return userRepository.save(user);
                })
                .doOnSuccess(user -> log.info("User with email {} set to ACTIVE successfully", email))
                .doOnError(error -> log.error("Failed to set user with email {} to ACTIVE: {}", email, error.getMessage()));
    }

    public Mono<Void> setUsersInactiveFromMessageBody(String messageBody) {
        String[] emails = messageBody.split(",");
        log.info("Setting users from message body to INACTIVE");
        return Flux.fromArray(emails)
                .map(String::trim)
                .flatMap(this::setInactiveByEmail)
                .then()
                .doOnSuccess(aVoid -> log.info("All users set to INACTIVE successfully"))
                .doOnError(error -> log.error("Error setting users to INACTIVE: {}", error.getMessage()));
    }

    public Mono<User> setInactiveByEmail(String email) {
        return userRepository.findById(email)
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("User with email {} not found", email)))
                .flatMap(user -> {
                    log.info("Deactivating user with email {}", email);
                    user.setStatus("INACTIVE");
                    return userRepository.save(user)
                            .doOnSuccess(savedUser -> log.info("User with email {} set to INACTIVE", email))
                            .doOnError(error -> log.error("Failed to set user with email {} to INACTIVE: {}", email, error.getMessage()));
                });
    }

    public Mono<Void> deleteUser(String email) {
        log.info("Deleting user with email: {}", email);
        return userRepository.findById(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with email: " + email)))
                .flatMap(user -> userRepository.deleteById(email))
                .then()
                .doOnSuccess(aVoid -> log.info("User with email {} deleted successfully", email))
                .doOnError(error -> log.error("Failed to delete user with email {}: {}", email, error.getMessage()));
    }
}
