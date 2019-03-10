package com.genuitec.webclipse.example.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.junit.Test;
import org.mockito.Mockito;

public class ExpectancyTest {
	
	@Test
	public void checkLifeExpectancyForFemales() throws IOException {
		List<String> collection = new ArrayList<String>();
		collection.add("United States");
		collection.add("China");
		collection.add("Russia");
		ExpectancyREST expectancyRest = Mockito.mock(ExpectancyREST.class);
		
		JSONArray array = new JSONArray(collection);
		when(expectancyRest.getCountries()).thenReturn(array);
		
		String sex = "female";
		when(expectancyRest.getLifeExpectancy(sex , "United States", "1992-01-01")).thenReturn("70.0");
		when(expectancyRest.getLifeExpectancy(sex , "China", "1992-01-01")).thenReturn("80.0");
		when(expectancyRest.getLifeExpectancy(sex , "Russia", "1992-01-01")).thenReturn("80.0");
		
		try {
			String result = expectancyRest.getLifeExpectancyForFemales("1992-01-01");
			assertEquals(result.substring(2).split(",")[0], "70.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
