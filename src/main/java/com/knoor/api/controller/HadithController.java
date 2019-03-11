package com.knoor.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.knoor.api.enums.ErrorMessageEnum;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.exception.RestException;
import com.knoor.api.model.DuplicateInfos;
import com.knoor.api.service.CloudMongoService;
import com.knoor.api.service.HadithService;
import com.knoor.api.service.reactive.HadithReactiveService;

@RestController
@RequestMapping("/knoor/api/v1/hadiths")
public class HadithController {

	private final Logger LOG = LoggerFactory.getLogger(HadithController.class);

	private static String HADITH_COLLECTION = "hadiths";

	@Autowired
	private HadithReactiveService hadithReactiveService;

	@Autowired
	HadithService hadithService;

	@PostMapping(path = "/duplicate")
	@ResponseBody
	public ResponseEntity<List<DuplicateInfos>> searchDuplicate() throws RestException {

		List<DuplicateInfos> result = new ArrayList<>();
		
		try {
			result = hadithService.getDuplicateHadith();
			LOG.info("Find duplicate docs: " + result.size());
		} catch (BusinessException e) {
			LOG.error(e.getMessage(), e);
			throw new RestException(ErrorMessageEnum.DUPLICATE_HADITH_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		}
		return ResponseEntity.ok(result);
	}

}
