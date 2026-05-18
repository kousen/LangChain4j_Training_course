package com.kousenit.langchain4j;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.util.Optional;

/**
 * WeatherTool demonstrates tools with parameters.
 * <p>
 * In a real implementation these would call a weather API like
 * OpenWeatherMap; here they return canned data so the LLM has something
 * deterministic to reason about.
 * <p>
 * Used in Lab 6: AI Tools exercises.
 */
public class WeatherTool {

    @Tool("Get the current weather for a specific city")
    public String getCurrentWeather(String city, String units) {
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
            days = 7;
        }

        return String.format(
                "Weather forecast for %s for the next %d days: "
                        + "Mostly sunny with temperatures ranging from 18-25°C. "
                        + "Light rain expected on day %d.",
                city, days, Math.max(1, days / 2));
    }

    /**
     * Demonstrates the {@code Optional<T>} tool parameter pattern (added in
     * LangChain4j 1.12). When the LLM omits the units, this method falls
     * back to metric. The same effect can be achieved with
     * {@code @P(required = false)}; {@code Optional<T>} is preferred when
     * the absence of a value is meaningful enough to deserve its own type.
     */
    @Tool("Get the current weather; units defaults to metric if not specified")
    public String getWeatherWithDefault(
            @P("City name") String city, @P("Unit system: metric or imperial") Optional<String> units) {
        String resolvedUnits = units.orElse("metric");
        return getCurrentWeather(city, resolvedUnits);
    }
}
