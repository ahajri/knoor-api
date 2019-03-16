package com.knoor.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.knoor.api.controller.handler.HadithHandler;

@Configuration
public class ApiRouter {

	
	@Bean
	public RouterFunction<ServerResponse> route(HadithHandler handler) {

		return RouterFunctions
			.route(RequestPredicates.GET("/asyncDuplicate")
					.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), 
						handler::handleFullDuplicate);
	}
}
