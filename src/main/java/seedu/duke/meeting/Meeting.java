package seedu.duke.meeting;

import seedu.duke.exception.FinanceProPlusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Meeting {
    private static final String MEETING_REGEX = "\\s+(?=[a-z]+/)";
    private String title;
    private String date;
    private String client;
    private String startTime;
    private String endTime;

    /**
     * Creates a Meeting from the provided arguments string.
     * Validates required fields and time formats.
     *
     * @param arguments The raw string of meeting details.
     * @throws FinanceProPlusException If required details are missing or formats are invalid.
     */
    public Meeting(String arguments) throws FinanceProPlusException {
        assert arguments != null && !arguments.trim().isEmpty() : "Arguments for meetings creation cannot be null";
        Map<String, String> detailsMap = parseMeetingDetails(arguments);
        List<String> requiredKeys = List.of("t", "c", "d", "from");
        for (String key : requiredKeys) {
            if (!detailsMap.containsKey(key) || detailsMap.get(key).isEmpty()) {
                throw new FinanceProPlusException("Invalid meeting details. Please provide all required fields: "
                        + "t/TITLE c/CLIENT d/DATE from/START_TIME");
            }
        }
        title = detailsMap.get("t");
        date = detailsMap.get("d");
        client = detailsMap.get("c");
        startTime = detailsMap.get("from");
        endTime = detailsMap.get("to");
        assert this.title != null && !title.isEmpty() : "Title should be initialised";
        assert this.client != null && !client.isEmpty() : "Client should be initialised";
        assert this.date != null && !date.isEmpty() : "Date should be initialised";

        validateDateFormat(date);
        validateTimeFormat(startTime);
        assert this.startTime != null && !startTime.isEmpty() : "Start time should be initialised";
        if (endTime != null) {
            validateTimeFormat(endTime);
            validateTimeOrder(startTime, endTime);
        }
    }

    private void validateDateFormat(String dateString) throws FinanceProPlusException {
        assert dateString != null && !dateString.isEmpty() : "Date string should not be null";
        if (!dateString.matches("\\d{2}-\\d{2}-\\d{4}")) {
            throw new FinanceProPlusException("Invalid date format. Please use dd-MM-yyyy (e.g., 24-10-2025)");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(dateString, formatter);
            String reformatted = parsedDate.format(formatter);
            if (!reformatted.equals(dateString)) {
                throw new FinanceProPlusException("Invalid date: " + dateString + ". Please provide a valid date.");
            }
        } catch (DateTimeParseException e) {
            throw new FinanceProPlusException("Invalid date: " + dateString + ". Please provide a valid date.");
        }
    }

    private void validateTimeFormat(String timeString) throws FinanceProPlusException {
        assert timeString != null && !timeString.isEmpty() : "Time string should not be null";
        if (!timeString.matches("\\d{2}:\\d{2}")) {
            throw new FinanceProPlusException("Invalid time format. Please use HH:mm (e.g., 14:30)");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime parsedTime = LocalTime.parse(timeString, formatter);
            String reformatted = parsedTime.format(formatter);
            if (!reformatted.equals(timeString)) {
                throw new FinanceProPlusException("Invalid time: " + timeString + ". Please provide a valid time.");
            }
        } catch (DateTimeParseException e) {
            throw new FinanceProPlusException("Invalid time: " + timeString + ". Please provide a valid time.");
        }
    }

    private void validateTimeOrder(String startTimeString, String endTimeString) throws FinanceProPlusException {
        assert startTimeString != null && !startTimeString.isEmpty() : "Start time should not be null";
        assert endTimeString != null && !endTimeString.isEmpty() : "End time should not be null";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(startTimeString, formatter);
        LocalTime endTime = LocalTime.parse(endTimeString, formatter);
        
        if (!startTime.isBefore(endTime)) {
            throw new FinanceProPlusException("Start time (" + startTimeString 
                    + ") must be before end time (" + endTimeString + ")");
        }
    }


    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }

    /**
     * Parses meeting details from a string into a map of key-value pairs.
     *
     * @param meetingDetails The raw string containing meeting details.
     * @return A map containing parsed meeting details.
     */
    public static Map<String, String> parseMeetingDetails(String meetingDetails) {
        assert meetingDetails != null : "Input string for parsing cannot be null";
        Map<String, String> detailsMap = new HashMap<>();
        String trimmedDetails = meetingDetails.trim();
        String[] parts = trimmedDetails.split(MEETING_REGEX);
        for (String part : parts) {
            String[] keyValue = part.split("/", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1].trim();
                detailsMap.put(key, value);
            }
        }
        assert detailsMap != null : "The resulting details map should not be null";
        return detailsMap;
    }


    @Override
    public String toString() {
        assert title != null && !title.isEmpty() : "Title should not be null";
        assert client != null && !client.isEmpty() : "Client should not be null";
        assert date != null && !date.isEmpty() : "Date should not be null";
        assert startTime != null && !startTime.isEmpty() : "Start time should not be null";
        String timeInfo = "";
        if (startTime != null && endTime != null) {
            timeInfo = ", Time: " + startTime + " to " + endTime;
        } else if (startTime != null) {
            timeInfo = ", Start Time: " + startTime;
        }
        return "Title: " + title + ", Client: " + client + ", Date: " + date + timeInfo;
    }

    /**
     * Converts the meeting to a storage format string.
     *
     * @return The meeting in storage format.
     */
    public String toStorageString() {

        StringBuilder sb = new StringBuilder();
        sb.append("t/").append(title)
                .append(" c/").append(client)
                .append(" d/").append(date)
                .append(" from/").append(startTime);
        if (endTime != null && !endTime.isEmpty()) {
            sb.append(" to/").append(endTime);
        }
        return sb.toString();
    }

    /**
     * Converts the meeting to a CSV row format.
     *
     * @return Array of strings representing the meeting data.
     */
    public String[] toCSVRow() {

        return new String[]{title, client, date, startTime, endTime == null ? "" : endTime};
    }

}
