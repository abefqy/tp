package seedu.duke.meeting;

import seedu.duke.container.ListContainer;
import seedu.duke.exception.FinanceProPlusException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class MeetingList implements ListContainer {
    private static final Logger logger = Logger.getLogger(MeetingList.class.getName());
    private ArrayList<Meeting> meetings;

    public MeetingList() {
        this.meetings = new ArrayList<>();
        assert meetings != null : "MeetingList should not be null after initialization";
    }

    @Override
    public void addItem(String arguments) throws FinanceProPlusException {
        Meeting meeting = new Meeting(arguments);
        assert meeting != null : "Meeting to be added should not be null";
        assert meetings != null : "MeetingList should not be null when adding an item";
        int oldSize = meetings.size();
        meetings.add(meeting);
        assert meetings.size() == oldSize + 1: "MeetingList size should increase by 1 after adding an item";
        System.out.println("Noted. I've added this meeting:");
        System.out.println(meeting.toString());
        logger.info("Successfully added new meeting: " + meeting.getTitle());
    }

    @Override
    public void addItem(String arguments, ListContainer listContainer) throws FinanceProPlusException {
        throw new FinanceProPlusException("Implemented only on ClientList class");
    }

    @Override
    public void deleteItem(String arguments) throws FinanceProPlusException {
        if (meetings.size() == 0) {
            System.out.println("No meetings to delete.");
            return;
        }
        int oldSize = meetings.size();
        int index = checkDeleteIndex(arguments);
        Meeting removedMeeting = meetings.remove(index);
        assert meetings.size() == oldSize - 1 : "Meeting list size should decrease by 1 after deleting a meeting";
        System.out.println("Noted. I've removed this meeting:");
        System.out.println(removedMeeting.toString());
        logger.info("Successfully deleted meeting: " + removedMeeting.getTitle());
    }

    @Override
    public void listItems() throws FinanceProPlusException {
        if (meetings.size() == 0) {
            System.out.println("No meetings found.");
        } else {
            System.out.println("Here are the meetings in your list:");
            for (int i = 0; i < meetings.size(); i++) {
                System.out.println((i + 1) + ". " + meetings.get(i).toString());
            }
        }
    }

    @Override
    public int checkDeleteIndex(String arguments) throws FinanceProPlusException {
        int index;
        try {
            index = Integer.parseInt(arguments) - 1;
            if (index < 0 || index >= meetings.size()) {
                throw new FinanceProPlusException("Invalid index. Please provide a valid meeting index to delete.");
            }
        } catch (NumberFormatException e) {
            throw new FinanceProPlusException("Invalid input. Please provide a valid meeting index to delete.");
        }
        logger.fine("Validated delete index: " + index);
        return index;
    }
    
    /**
     * Converts all meetings to storage format.
     *
     * @return List of strings in storage format.
     */
    public List<String> toStorageFormat() {
        List<String> lines = new ArrayList<>();
        for (Meeting m : meetings) {
            lines.add(m.toStorageString());
        }
        return lines;
    }

    /**
     * Loads meetings from storage format.
     *
     * @param lines List of strings in storage format.
     * @throws FinanceProPlusException If any meeting data is invalid.
     */
    public void loadFromStorage(List<String> lines) throws FinanceProPlusException {
        for (String line : lines) {
            meetings.add(new Meeting(line));
        }
    }

    /**
     * Converts all meetings to CSV format.
     *
     * @return List of string arrays for CSV export.
     */
    public List<String[]> toCSVFormat() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Title", "Client", "Date", "Start Time", "End Time"});
        for (Meeting m : meetings) {
            rows.add(m.toCSVRow());
        }
        return rows;
    }


    /**
     * Lists meetings scheduled in the next 7 days.
     */
    public void listForecast() {
        assert meetings != null : "Meetings list should not be null";
        LocalDate today = LocalDate.now();
        LocalDate forecastEnd = today.plusDays(7);
        assert forecastEnd.isAfter(today) : "Forecast end date should be after today's date";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        ArrayList<Meeting> forecastMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            LocalDate meetingDate = LocalDate.parse(meeting.getDate(), formatter);
            if (!meetingDate.isBefore(today) && !meetingDate.isAfter(forecastEnd)) {
                forecastMeetings.add(meeting);
            }
        }

        if (forecastMeetings.isEmpty()) {
            System.out.println("No meetings scheduled in the next 7 days.");
        } else {
            System.out.println("Meetings in the next 7 days:");
            for (int i = 0; i < forecastMeetings.size(); i++) {
                System.out.println((i + 1) + ". " + forecastMeetings.get(i).toString());
            }
        }
    }
}
