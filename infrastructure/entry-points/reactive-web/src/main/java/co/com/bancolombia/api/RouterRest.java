package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/users"), handler::saveUser)
                .andRoute(GET("/users/{email}"), handler::findUserByEmail)
                .and(route(GET("/users"), handler::getAllUsers))
                .and(route(PUT("/users/active-user/{email}"), handler::setActiveByEmail))
                .and(route(PUT("/users/inactive-user/{email}"), handler::setInactiveByEmail))
                .and(route(DELETE("/users/delete-user/{email}"), handler::deleteUser));
    }
}
