package com.kousenit.langchain4j;

import dev.langchain4j.agent.tool.Tool;

/**
 * WeatherTool demonstrates tools with parameters.
 * <p>
 * This tool shows how AI can call methods with specific arguments
 * for dynamic functionality. In a real implementation, this would
 * integrate with actual weather APIs.
 * <p>
 * Used in Lab 6: AI Tools exercises.
 */
public class WeatherTool {

    @Tool("Get the current weather for a specific city")
    public String getCurrentWeather(String city, String units) {
        // In a real implementation, this would call a weather API like OpenWeatherMap
        String tempUnit = units.equals("metric") ? "C" : "F";
        int temperature = units.equals("metric") ? 22 : 72;

        return String.format(
                "The current weather in %s is %d°%s and sunny with light clouds. "
                        + "Humidity is 65%% and wind speed is 10 km/h.",
                city, temperature, tempUnit);
    }

    @Tool("Get weather forecast for a city for the next few days")
    public String getWeatherForecast(String city, int days) {
        if (days > 7) {
            days = 7; // Limit to 7 days maximum
        }

        return String.format(
                "Weather forecast for %s for the next %d days: "
                        + "Mostly sunny with temperatures ranging from 18-25°C. "
                        + "Light rain expected on day %d.",
                city, days, Math.max(1, days / 2));
    }
}
