package com.knoor.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.knoor.api.enums.ErrorMessageEnum;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.exception.RestException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithModel;
import com.knoor.api.service.HadithService;
import com.knoor.api.service.reactive.HadithReactiveService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/hadith")
public class HadithController {

	private final Logger LOG = LoggerFactory.getLogger(HadithController.class);

	@Autowired
	private WebClient webClient;

	@Autowired
	private HadithReactiveService hadithReactiveService;

	@Autowired
	HadithService hadithService;

	@GetMapping(path = "/duplicate")
	@ResponseBody
	public ResponseEntity<List<DuplicateInfos>> searchDuplicate() throws RestException {

		// ListenableFuture<List<DuplicateInfos>> listenableFuture =
		// getRequest.execute(new AsyncCompletionHandler<List<DuplicateInfos>>() {
		// @Override
		// public String onCompleted(Response response) throws Exception {
		// LOG.info("Async Non Blocking Request processing completed");
		// return "Async Non blocking...";
		// }
		// });
		// return listenableFuture.toCompletableFuture();

		try {
			// result.addAll(hadithService.searchFullDuplicate());
			List<DuplicateInfos> result = hadithService.searchFullDuplicate();
			LOG.info("full duplicate hadiths found ====>" + result.size());
			return ResponseEntity.ok(result);
			// LOG.info("===>full uplicate Hadiths Count: " + result.size());
		} catch (BusinessException e) {
			LOG.error("Oooops", e);
			throw new RestException(ErrorMessageEnum.DUPLICATE_HADITH_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		} 

	}

	@GetMapping("/all")
	public Flux<String> findAll() {

		Flux<HadithModel> result = hadithReactiveService.reactiveFindAll();
		return result.map(m -> m.getHadith());
	}

//	@GetMapping(value = "/customer/accounts/{pesel}")
//	public Mono<DuplicateInfos> findByPeselWithAccounts() {
//		return repository.findByPesel()
//				.flatMap(customer -> webClient.get().uri("/account/customer/{customer}", customer.getId())
//						.accept(MediaType.APPLICATION_JSON).exchange()
//						.flatMap(response -> response.bodyToFlux(Account.class)))
//				.collectList().map(l -> {
//					return new Customer(pesel, l);
//				});
//	}

	@GetMapping(path = "/async/duplicate")
	@ResponseBody
	public Flux<DuplicateInfos> searchAsyncDuplicate() throws RestException {
		try {
			return hadithReactiveService.reactiveFullDuplicate();
		} catch (BusinessException e) {
			LOG.error("Ooops", e);
			throw new RestException(ErrorMessageEnum.DUPLICATE_HADITH_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		} 
	}

}
