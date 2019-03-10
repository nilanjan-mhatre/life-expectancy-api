package com.genuitec.webclipse.example.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author MyEclipse Web Service Tools
 */
@javax.inject.Singleton
@Path("/")
@WebService
//@Getter
//@Setter
public class ExpectancyREST {

	private static final String BASE = "http://api.population.io/1.0/";
	private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

	public ExpectancyREST() {
	}

	@GET
	@Path("/countries")
	@Produces({ "application/json" })
	public String findCountries() throws IOException {
		StringBuilder countryList = new StringBuilder("[");
		try {

			JSONArray array = getCountries();
			int i;
			for (i = 0; i < array.length(); i++) {
				countryList.append("\"");
				countryList.append(array.getString(i));
				countryList.append("\"");
				countryList.append(",");
			}
			countryList.deleteCharAt(countryList.length() - 1);
			countryList.append("]");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return countryList.toString();
	}

	public JSONArray getCountries() {
		URL url;
		JSONArray array = null;
		try {
			url = new URL(BASE + "countries");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Accept", "application/json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	
			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);
	
			array = obj.getJSONArray("countries");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}
		return array;
	}
	
	@GET
	@Path("/min-life-expectancy-for-females/{date}")
	@Produces({ "application/json" })
	public String getLifeExpectancyForFemales(@PathParam("date") String dateStr) {
		Double[] lifeExpectancy = new Double[2];
		lifeExpectancy[0] = Double.MAX_VALUE;
		String country = null;
		try {

			JSONArray array = getCountries();
			int i;
			String current = null;
			for (i = 0; i < array.length(); i++) {
				Double temp = Double.MAX_VALUE;
				current = array.getString(i);
				try {
					temp = Double.parseDouble(getLifeExpectancy("female", current, dateStr));
					if(temp < lifeExpectancy[0]) {
						lifeExpectancy[0] = temp;
						country = current;
					}
				} catch(NumberFormatException e) {
				}
			}
			try {
				lifeExpectancy[1] = Double.parseDouble(getLifeExpectancy("female", country, fmt.format(new Date())));
			} catch(NumberFormatException e) {
				return "{0.0, 0.0}";
			}
		} catch (IOException e) {
		}
		return "{" + lifeExpectancy[0] + "," + lifeExpectancy[1] + "};" + country;
	}
	
	@GET
	@Path("/life-expectancy/total/{sex}/{country}/{date}")
	@Produces({ "application/json" })
	public String getLifeExpectancy(@PathParam("sex") String sex, @PathParam("country") String country,
			@PathParam("date") String dateStr) throws IOException {
		BufferedReader reader = null;
		try {
			fmt.parse(dateStr);
		} catch (ParseException e1) {
			return "{0.0, 0.0}";
		}
		try {

			URL url = new URL(BASE + "life-expectancy/total/" + sex + "/" + "" + country.replaceAll(" ", "%20") + "/" + dateStr);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Accept", "application/json");
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			String line = reader.readLine();
			JSONObject obj = new JSONObject(line);
			return new Double(obj.getDouble("total_life_expectancy")).toString();

		} catch (Exception e) {
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return "{0.0, 0.0}";
	}

}

class CountryList {
	private List<String> countries;

	public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}

}