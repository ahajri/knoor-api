package com.knoor.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.knoor.api.service.HadithService;

@Configuration
public class ApiRouter {

	
//	@Bean
//	public RouterFunction<ServerResponse> route(HadithService hadithService) {
//
//		return RouterFunctions
//			.route(RequestPredicates.GET("/api/v1/hadith/reactive/duplicate").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), hadithService::reactiveFullDuplicate);
//	}
}
