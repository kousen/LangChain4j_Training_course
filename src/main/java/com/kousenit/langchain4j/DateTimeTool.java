package com.kousenit.langchain4j;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DateTimeTool provides date and time related functionality for AI assistants.
 * <p>
 * This is a reference implementation that demonstrates the @Tool annotation pattern.
 * Students can use this as an example when implementing their own tools in Lab 6.
 * <p>
 * Used in Lab 4 (personalAssistantWithMemoryAndTools) and extensively in Lab 6 (AI Tools).
 */
public class DateTimeTool {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeTool.class);
    
    @Tool("Get the current date and time")
    public String getCurrentDateTime() {
        logger.info("Getting current date and time");
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Tool("Get the date that is a specified number of years from now")
    public String getDateYearsFromNow(int years) {
        logger.info("Calculating date {} years from now", years);
        LocalDate futureDate = LocalDate.now().plusYears(years);
        return futureDate.toString();
    }

    @Tool("Get the date that is a specified number of days from now")
    public String getDateDaysFromNow(int days) {
        logger.info("Calculating date {} days from now", days);
        LocalDate futureDate = LocalDate.now().plusDays(days);
        return futureDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
    }

    @Tool("Set an alarm for a specific time")
    public String setAlarm(String time) {
        logger.info("Setting alarm for {}", time);
        // In a real implementation, this would integrate with system alarm/notification APIs
        return "Alarm set for " + time + ". You will be notified at the specified time.";
    }

    @Tool("Get the current year")
    public int getCurrentYear() {
        logger.info("Getting current year");
        return LocalDate.now().getYear();
    }
}