package com.knoor.soft.knoorapi.config.batch;

import java.util.List;

import org.springframework.batch.item.file.LineMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.knoor.soft.knoorapi.batch.model.BCountry;
/**
 * 
 * @author ahajri
 *
 */
public class BCountryJsonLineMapper implements LineMapper<List<BCountry>> {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public List<BCountry> mapLine(String line, int lineNumber) throws Exception {
		CollectionType collectionType =	mapper.getTypeFactory().constructCollectionType(List.class, BCountry.class);
		return mapper.readValue(line, collectionType);
	}

}
