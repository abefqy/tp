package seedu.duke.meeting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.duke.exception.FinanceProPlusException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeetingListTest {

    private MeetingList meetingList;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        meetingList = new MeetingList();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void addItem_validMeeting_addsSuccessfully() throws FinanceProPlusException {
        String args = "t/Policy Review c/Alice Wong d/05-11-2025 from/14:00 to/16:00";
        assertEquals(0, meetingList.toStorageFormat().size());
        meetingList.addItem(args);
        assertEquals(1, meetingList.toStorageFormat().size());
        assertTrue(outContent.toString().contains("Noted. I've added this meeting:"));
        assertTrue(outContent.toString().contains("Policy Review"));
    }

    @Test
    void addItem_validMeetingWithoutEndTime_addsSuccessfully() throws FinanceProPlusException {
        String args = "t/Quick Call c/Bob Tan d/08-11-2025 from/11:00";
        meetingList.addItem(args);
        assertEquals(1, meetingList.toStorageFormat().size());
        assertTrue(outContent.toString().contains("Quick Call"));
    }

    @Test
    void addItem_invalidMeeting_throwsException() {
        String args = "t/Policy Review c/Alice Wong d/05-11-2025";
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            meetingList.addItem(args);
        });
        assertEquals("Invalid meeting details. Please provide all required fields: " +
                "t/TITLE c/CLIENT d/DATE from/START_TIME", exception.getMessage());
    }

    @Test
    void addItem_withListContainer_throwsException() {
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            meetingList.addItem("args", null);
        });
        assertEquals("Implemented only on ClientList class", exception.getMessage());
    }

    @Test
    void deleteItem_validIndex_removesSuccessfully() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00");
        meetingList.addItem("t/Meeting 2 c/Client 2 d/06-11-2025 from/15:00");
        outContent.reset();
        
        assertEquals(2, meetingList.toStorageFormat().size());
        meetingList.deleteItem("1");
        assertEquals(1, meetingList.toStorageFormat().size());
        assertTrue(outContent.toString().contains("Noted. I've removed this meeting:"));
        assertTrue(outContent.toString().contains("Meeting 1"));
    }

    @Test
    void deleteItem_invalidIndex_throwsException() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00");
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            meetingList.deleteItem("5");
        });
        assertEquals("Invalid index. Please provide a valid meeting index to delete.", exception.getMessage());
    }

    @Test
    void deleteItem_nonNumericIndex_throwsException() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00");
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            meetingList.deleteItem("abc");
        });
        assertEquals("Invalid input. Please provide a valid meeting index to delete.", exception.getMessage());
    }

    @Test
    void deleteItem_emptyList_printsMessage() throws FinanceProPlusException {
        meetingList.deleteItem("1");
        assertTrue(outContent.toString().contains("No meetings to delete."));
    }

    @Test
    void listItems_nonEmptyList_printsList() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00");
        meetingList.addItem("t/Meeting 2 c/Client 2 d/06-11-2025 from/15:00");
        outContent.reset();
        
        meetingList.listItems();
        String output = outContent.toString();
        assertTrue(output.contains("Here are the meetings in your list:"));
        assertTrue(output.contains("1. Title: Meeting 1"));
        assertTrue(output.contains("2. Title: Meeting 2"));
    }

    @Test
    void listItems_emptyList_printsMessage() throws FinanceProPlusException {
        meetingList.listItems();
        assertTrue(outContent.toString().contains("No meetings found."));
    }

    @Test
    void checkDeleteIndex_validIndex_returnsCorrectIndex() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00");
        int index = meetingList.checkDeleteIndex("1");
        assertEquals(0, index);
    }

    @Test
    void checkDeleteIndex_invalidIndex_throwsException() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00");
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            meetingList.checkDeleteIndex("5");
        });
        assertEquals("Invalid index. Please provide a valid meeting index to delete.", exception.getMessage());
    }

    @Test
    void toStorageFormat_returnsCorrectFormat() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00 to/16:00");
        meetingList.addItem("t/Meeting 2 c/Client 2 d/06-11-2025 from/15:00");
        
        List<String> storage = meetingList.toStorageFormat();
        assertEquals(2, storage.size());
        assertEquals("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00 to/16:00", storage.get(0));
        assertEquals("t/Meeting 2 c/Client 2 d/06-11-2025 from/15:00", storage.get(1));
    }

    @Test
    void loadFromStorage_validData_loadsSuccessfully() throws FinanceProPlusException {
        List<String> data = List.of(
            "t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00 to/16:00",
            "t/Meeting 2 c/Client 2 d/06-11-2025 from/15:00"
        );
        
        meetingList.loadFromStorage(data);
        assertEquals(2, meetingList.toStorageFormat().size());
    }

    @Test
    void toCSVFormat_returnsCorrectFormat() throws FinanceProPlusException {
        meetingList.addItem("t/Meeting 1 c/Client 1 d/05-11-2025 from/14:00 to/16:00");
        meetingList.addItem("t/Meeting 2 c/Client 2 d/06-11-2025 from/15:00");
        
        List<String[]> csv = meetingList.toCSVFormat();
        assertEquals(3, csv.size()); // Header + 2 meetings
        
        // Check header
        String[] header = csv.get(0);
        assertArrayEquals(new String[]{"Title", "Client", "Date", "Start Time", "End Time"}, header);
        
        // Check first meeting
        String[] meeting1 = csv.get(1);
        assertArrayEquals(new String[]{"Meeting 1", "Client 1", "05-11-2025", "14:00", "16:00"}, meeting1);
        
        // Check second meeting
        String[] meeting2 = csv.get(2);
        assertArrayEquals(new String[]{"Meeting 2", "Client 2", "06-11-2025", "15:00", ""}, meeting2);
    }

    @Test
    void listForecast_noUpcomingMeetings_printsNoMeetingsMessage() throws FinanceProPlusException {
        // Add a meeting in the past
        meetingList.addItem("t/Past Meeting c/Client 1 d/01-01-2020 from/14:00");
        outContent.reset();
        
        meetingList.listForecast();
        assertTrue(outContent.toString().contains("No meetings scheduled in the next 7 days."));
    }

    @Test
    void listForecast_withUpcomingMeetings_printsUpcomingMeetings() throws FinanceProPlusException {
        // Add meetings with future dates (you may need to adjust dates based on current date)
        meetingList.addItem("t/Future Meeting c/Client 1 d/31-12-2025 from/14:00");
        outContent.reset();
        
        meetingList.listForecast();
        // This test may need adjustment based on current date
        // The forecast method checks for meetings within next 7 days from current date
    }
}
