package com.capo.facturas_sinteticas.utils;

import org.springframework.http.codec.ServerSentEvent;

import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.response.GenerationSyntheticDataResponse;

public class ConverterUtil {
	
	public static GenerationSyntheticDataResponse getGenerationSyntheticDataResponse(String message) {
		GenerationSyntheticDataResponse response= new GenerationSyntheticDataResponse();
		response.setResponse(message);
		return response;
	} 
	
	public static DataMessage setDataMessage(String data) {
		DataMessage dataMessage= new DataMessage();
		dataMessage.setMessage(data);
		return dataMessage;
	}
	
	public static ServerSentEvent<DataMessage> setServerSentEvent(DataMessage data, String eventName) {
		return ServerSentEvent.<DataMessage>builder()
				.id("1")
				.comment("prueba")
				.event(eventName)
			    .data(data)
			    .build();
	}
}
