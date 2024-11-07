import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName){

        // Obter as coordenadas da localidade usando geolocation API
        JSONArray locationData = getLocationData(locationName);

        // Obter latitude e longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?" + "latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);

            assert conn != null;
            if (conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            StringBuilder resultJSON = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());

            while (scanner.hasNext()){
                resultJSON.append(scanner.nextLine());
            }

            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

            // ==== Obtendo dados ====//
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weather_code = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weather_code.get(index));

            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);


            // Object dos dados JSON para acesso no front-end
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);

            return weatherData;



        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData (String locationName){
        locationName = locationName.replaceAll(" ","+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);

            assert conn != null;
            if (conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            } else {

                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNext()){
                    resultJSON.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

                return (JSONArray) resultsJsonObj.get("results");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Função que conecta com a API
    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        for (int i=0; i < timeList.size();i++){
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        LocalDateTime currentDataTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDataTime.format(formatter);
    }

    private static String convertWeatherCode(long weather_code){
        String weatherCondition = "";
        if(weather_code == 0L){
            weatherCondition = "Clear";
        }else if(weather_code > 0L && weather_code <= 3L){
            weatherCondition = "Cloudy";
        }else if((weather_code >= 51L && weather_code <= 67L) || (weather_code >= 80L && weather_code <= 99L)){
            weatherCondition = "Rain";
        }else if(weather_code >= 71L && weather_code <= 77L){
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }


}







