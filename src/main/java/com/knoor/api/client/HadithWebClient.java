package com.knoor.api.client;

import org.apache.commons.httpclient.Header;
import org.apache.http.client.methods.HttpHead;
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
			.uri(rootApiUrl+"/asyncDuplicate")
			.accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYXJpbS5hdGlndWlAZ21haWwuY29tIiwiaXNzIjoiS05PT1ItQVBJIiwiZXhwIjoxNTUyODE1MjYyLCJpYXQiOjE1NTI3Mjg4NjJ9.MjhnO9OXyNRHiNqpRnyPTWfsQY8WjeLORN5upFVMpsnSnUUVui6Hk90mXuZPpec2yxyOppji3VYnYPvVpBELRQ")
			.exchange();

	public String getResult() {
		return ">> result = " + result.flatMap(res -> res.bodyToMono(DuplicateInfos.class)).block();
	}
}
