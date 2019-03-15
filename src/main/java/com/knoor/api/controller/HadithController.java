package com.knoor.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.knoor.api.enums.ErrorMessageEnum;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.exception.RestException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.model.HadithCount;
import com.knoor.api.service.HadithService;
import com.knoor.api.service.reactive.HadithReactiveService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/hadith")
public class HadithController {

	private final Logger LOG = LoggerFactory.getLogger(HadithController.class);


	@Autowired
	private HadithReactiveService hadithReactiveService;
	

	@Autowired
	HadithService hadithService;

	@GetMapping(path = "/duplicate")
	@ResponseBody
	public ResponseEntity<List<DuplicateInfos>> searchDuplicate() throws RestException {

//		ListenableFuture<List<DuplicateInfos>> listenableFuture = getRequest.execute(new AsyncCompletionHandler<List<DuplicateInfos>>() {
//            @Override
//            public String onCompleted(Response response) throws Exception {
//                LOG.info("Async Non Blocking Request processing completed");
//                return "Async Non blocking...";
//            }
//        });
//        return listenableFuture.toCompletableFuture();
		
		try {
			//result.addAll(hadithService.searchFullDuplicate());
			List<DuplicateInfos> result = hadithService.getDuplicateHadith();
			LOG.info("full duplicate hadiths found ====>"+result.size());
			return ResponseEntity.ok(result);
			//LOG.info("===>full uplicate Hadiths Count: " + result.size());
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new RestException(ErrorMessageEnum.DUPLICATE_HADITH_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		}catch (Exception e) {
			LOG.error("=====Grrrrrrr", e);
			throw new RestException(ErrorMessageEnum.DUPLICATE_HADITH_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		}
		
	}
	
	
	
	@GetMapping(path = "/asyncDuplicate")
	@ResponseBody
	public ResponseEntity<List<DuplicateInfos>> searchAsyncDuplicate() throws RestException {
		final List<DuplicateInfos> result = new ArrayList<>();
		try {
			Flux<DuplicateInfos> duplicates=hadithReactiveService.asyncFullDuplicates();
			duplicates.log().subscribe(result::add);
			LOG.info("===>Duplicate Hadiths Count: " + result.size());
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new RestException(ErrorMessageEnum.DUPLICATE_HADITH_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		} catch (Exception e) {
			LOG.error("Grrrrrrr", e);
			throw new RestException(ErrorMessageEnum.DUPLICATE_HADITH_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		}
		return ResponseEntity.ok(result);
	}

}
