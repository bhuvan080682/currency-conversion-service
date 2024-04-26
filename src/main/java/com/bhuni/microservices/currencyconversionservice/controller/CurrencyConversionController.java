package com.bhuni.microservices.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bhuni.microservices.currencyconversionservice.controller.bean.CurrencyConversion;
import com.bhuni.microservices.currencyconversionservice.proxy.CurrencyConversionProxy;

@RestController
public class CurrencyConversionController {
	@Autowired
	private CurrencyConversionProxy proxy;
	
	private Logger logger = LogManager.getLogger(CurrencyConversionController.class);
	
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversion(
			@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity) {
		logger.info("Called calculateCurrencyConversion.......");
		logger.info("from : {}, to : {} quantity : {}" + from,to,quantity);
		HashMap<String,String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		logger.info("Calling Rest...");
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/USD/to/INR", CurrencyConversion.class,uriVariables);
		CurrencyConversion currencyConversion = responseEntity.getBody();
		return new CurrencyConversion(currencyConversion.getId(), from, to, 
				quantity, currencyConversion.getConversionMultiple(),quantity.multiply(currencyConversion.getConversionMultiple()), 
				currencyConversion.getEnvironment());
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversionFeign(
			@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity) {
		/*HashMap<String,String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/USD/to/INR", CurrencyConversion.class,uriVariables);*/
		CurrencyConversion currencyConversion = proxy.retriveExchangeValue(from, to);
		//CurrencyConversion currencyConversion = CurrencyConversion currencyConversion = responseEntity.getBody();
		return new CurrencyConversion(currencyConversion.getId(), from, to, 
				quantity, currencyConversion.getConversionMultiple(),quantity.multiply(currencyConversion.getConversionMultiple()), 
				currencyConversion.getEnvironment());
	}
	

}
