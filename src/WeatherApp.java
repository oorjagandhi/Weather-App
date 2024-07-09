import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// Retrieve the weather data from the API - will fetch the latest weather data from external API
public class WeatherApp {
    // Fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        // Create a new instance of WeatherData
        JSONArray locationData = getLocationData(locationName);

        // Extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Pacific%2FAuckland";

        try {
            // Call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check for response success
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // Store the API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // Read the response from the API
                while(scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                // Close scanner
                scanner.close();

                // Close URL connection
                conn.disconnect();

                // Parse the JSON string into a JSON object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // Get the weather data from the API
                JSONObject hourly = (JSONObject) resultsJsonObj.get("hourly");


                // Get the index of our current hour
                JSONArray time = (JSONArray) hourly.get("time");
                int index = findIndextOfCurrentTime(time);

                // Get the temperature data for the current hour
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                double temperature = (double) (temperatureData.get(index));

                // Get the weather code
                JSONArray weatherCodeData = (JSONArray) hourly.get("weather_code");
                String weatherCondition = convertWeatherCode((long) weatherCodeData.get(index));

                // Get the humidity data for the current hour
                JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
                long humidity = (long) relativeHumidity.get(index);

                // Get windspeed
                JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
                double windSpeed = (double) windSpeedData.get(index);

                // Build a JSON object with the weather data
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windSpeed);

                return weatherData;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Fetch location data for given location
    // Fetch location data for given location
    public static JSONArray getLocationData(String locationName) {
        // Replace any whitespace in the location name to + to adhere to API request format
        locationName = locationName.replaceAll(" ", "+");

        // Build the URL for the API request
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try {
            // Call API and get a response
            HttpURLConnection conn = fetchApiResponse(url);

            // Check if the response is successful
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // Store the API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // Read the response from the API
                while(scanner.hasNextLine()) {
                    resultJson.append(scanner.nextLine());
                }

                // Close scanner
                scanner.close();

                // Close URL connection
                conn.disconnect();

                // Parse the JSON string into a JSON object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());

                // Get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // Create a URL object
            URL url = new URL(urlString);

            // Open a connection to the URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            conn.setRequestMethod("GET");

            // Connect to our API
            conn.connect();
            return conn;

        } catch(IOException e) {
            e.printStackTrace();
        }

        // Could not make connection
        return null;
    }

    private static int findIndextOfCurrentTime(JSONArray timeList) {
        // Get the current time in milliseconds
        String currentTime = getCurrentTime();

        // Iterate through the time list to find the index of the current time
        for (int i = 0; i < timeList.size(); i++) {
            // Get the time at the current index
            String time = (String) timeList.get(i);

            // Check if the current time matches the time at the current index
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        // Get the current time in milliseconds
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format date to be in the same format as the API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");

        // Format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // Convert weather code to be readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            weatherCondition = "clear";
        } else if(weathercode > 0L && weathercode <= 3L){
            weatherCondition = "cloudy";
        } else if((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)){
            weatherCondition = "rain";
        } else if(weathercode >= 71L && weathercode <= 77L){
            weatherCondition = "snow";
        }

        return weatherCondition;
    }

}
