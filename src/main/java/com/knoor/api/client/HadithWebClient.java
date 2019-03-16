package com.knoor.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.knoor.api.model.DuplicateInfos;

import reactor.core.publisher.Mono;

public class HadithWebClient {
	
	@Value("${application.root.url}")
	protected String rootApiUrl;
	
	
	private WebClient client = WebClient.create(rootApiUrl);

	private Mono<ClientResponse> result = client.get()
			.uri("/api/v1/hadith/duplicate1")
			.accept(MediaType.APPLICATION_JSON)
			.exchange();

	public String getResult() {
		return ">> result = " + result.flatMap(res -> res.bodyToMono(DuplicateInfos.class)).block();
	}
}
