package com.knoor.api.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.knoor.api.beans.QueryParam;
import com.knoor.api.enums.ErrorMessageEnum;
import com.knoor.api.exception.BusinessException;
import com.knoor.api.exception.RestException;
import com.knoor.api.service.CloudApiMongoService;
import com.knoor.api.service.CloudMongoService;
import com.knoor.api.utils.HttpUtils;
import com.knoor.api.utils.JsonUtils;

@RestController
@RequestMapping("/api/v1/event")
public class EventsController {

	private static final String EVENT_COLLECTION = "event";

	private static final Logger LOG = LoggerFactory.getLogger(EventsController.class);

	@Autowired
	private CloudApiMongoService cloudApiMongoService;

	@Autowired
	private CloudMongoService cloudMongoService;

	@RequestMapping(value = "/cloud/create", method = RequestMethod.POST)
	public ResponseEntity<Document> createEvent(@RequestBody Map<String, Object> eventMap) throws RestException {
		try {
			HttpResponse response = cloudApiMongoService.insertOne(EVENT_COLLECTION, eventMap);
			HttpEntity entity = response.getEntity();

			int httpCode = response.getStatusLine().getStatusCode();

			HttpStatus httpStatus = HttpUtils.statusCode2Status(httpCode);

			if (entity != null) {
				InputStream instream = entity.getContent();
				Document data = null;
				try {
					data = JsonUtils.load(instream, Document.class);
				} finally {
					instream.close();
				}
				return new ResponseEntity<Document>(data, httpStatus);
			}
			return new ResponseEntity<Document>(httpStatus);
		} catch (BusinessException | UnsupportedOperationException | IOException e) {
			throw new RestException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR,
					StringUtils.newStringUtf8("".getBytes()));
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Document> cloudCreate(@RequestBody Document eventMap) throws RestException {
		try {
			cloudMongoService.insertOne(EVENT_COLLECTION, eventMap);
			return new ResponseEntity<Document>(HttpStatus.OK);
		} catch (BusinessException e) {
			throw new RestException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR,
					StringUtils.newStringUtf8("".getBytes()));
		}
	}
	
	@PostMapping(path = "/search")
	@ResponseBody
	public ResponseEntity<List<Document>> searchEvents(final @RequestBody QueryParam... qp) throws RestException {
		List<Document> result = new ArrayList<>();
		final Gson gson = new Gson();
		try {
			result = cloudMongoService.search(EVENT_COLLECTION, qp).stream()
					.map(d -> gson.fromJson(gson.toJson(d), Document.class)).collect(Collectors.toList());
		} catch (BusinessException e) {
			LOG.error(e.getMessage(),e);
			throw new RestException(ErrorMessageEnum.SEARCH_USER_KO.getMessage(e.getMessage()), e,
					HttpStatus.NOT_FOUND, null);
		}
		return ResponseEntity.ok(result);
	}

}
