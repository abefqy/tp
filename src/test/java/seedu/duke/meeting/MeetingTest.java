package seedu.duke.meeting;

import org.junit.jupiter.api.Test;
import seedu.duke.exception.FinanceProPlusException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeetingTest {

    @Test
    void constructor_validInput_createsMeeting() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Policy Review c/John Doe d/05-11-2025 from/14:00 to/16:00");
        assertEquals("Policy Review", meeting.getTitle());
        assertEquals("05-11-2025", meeting.getDate());
    }

    @Test
    void constructor_validInputWithoutEndTime_createsMeeting() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Quick Call c/Jane Smith d/08-11-2025 from/11:00");
        assertEquals("Quick Call", meeting.getTitle());
        assertEquals("08-11-2025", meeting.getDate());
    }

    @Test
    void constructor_missingTitle_throwsException() {
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            new Meeting("c/John Doe d/05-11-2025 from/14:00");
        });
        assertEquals("Invalid meeting details. Please provide all required fields: " +
                "t/TITLE c/CLIENT d/DATE from/START_TIME", exception.getMessage());
    }

    @Test
    void constructor_startTimeAfterEndTime_throwsException() {
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            new Meeting("t/Policy Review c/John Doe d/05-11-2025 from/16:00 to/14:00");
        });
        assertEquals("Start time (16:00) must be before end time (14:00)", exception.getMessage());
    }

    @Test
    void toString_withEndTime_returnsCorrectFormat() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Policy Review c/John Doe d/05-11-2025 from/14:00 to/16:00");
        String expected = "Title: Policy Review, Client: John Doe, Date: 05-11-2025, Time: 14:00 to 16:00";
        assertEquals(expected, meeting.toString());
    }

    @Test
    void toString_withoutEndTime_returnsCorrectFormat() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Quick Call c/Jane Smith d/08-11-2025 from/11:00");
        String expected = "Title: Quick Call, Client: Jane Smith, Date: 08-11-2025, Start Time: 11:00";
        assertEquals(expected, meeting.toString());
    }

    @Test
    void toStorageString_withEndTime_returnsCorrectFormat() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Policy Review c/John Doe d/05-11-2025 from/14:00 to/16:00");
        String expected = "t/Policy Review c/John Doe d/05-11-2025 from/14:00 to/16:00";
        assertEquals(expected, meeting.toStorageString());
    }

    @Test
    void toStorageString_withoutEndTime_returnsCorrectFormat() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Quick Call c/Jane Smith d/08-11-2025 from/11:00");
        String expected = "t/Quick Call c/Jane Smith d/08-11-2025 from/11:00";
        assertEquals(expected, meeting.toStorageString());
    }

    @Test
    void toCSVRow_withEndTime_returnsCorrectArray() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Policy Review c/John Doe d/05-11-2025 from/14:00 to/16:00");
        String[] expected = {"Policy Review", "John Doe", "05-11-2025", "14:00", "16:00"};
        assertArrayEquals(expected, meeting.toCSVRow());
    }

    @Test
    void toCSVRow_withoutEndTime_returnsCorrectArray() throws FinanceProPlusException {
        Meeting meeting = new Meeting("t/Quick Call c/Jane Smith d/08-11-2025 from/11:00");
        String[] expected = {"Quick Call", "Jane Smith", "08-11-2025", "11:00", ""};
        assertArrayEquals(expected, meeting.toCSVRow());
    }

    @Test
    void parseMeetingDetails_validInput_returnsCorrectMap() {
        String input = "t/Policy Review c/John Doe d/05-11-2025 from/14:00 to/16:00";
        var result = Meeting.parseMeetingDetails(input);
        assertEquals("Policy Review", result.get("t"));
        assertEquals("John Doe", result.get("c"));
        assertEquals("05-11-2025", result.get("d"));
        assertEquals("14:00", result.get("from"));
        assertEquals("16:00", result.get("to"));
    }

    @Test
    void parseMeetingDetails_inputWithoutEndTime_returnsCorrectMap() {
        String input = "t/Quick Call c/Jane Smith d/08-11-2025 from/11:00";
        var result = Meeting.parseMeetingDetails(input);
        assertEquals("Quick Call", result.get("t"));
        assertEquals("Jane Smith", result.get("c"));
        assertEquals("08-11-2025", result.get("d"));
        assertEquals("11:00", result.get("from"));
        assertNull(result.get("to"));
    }
}
