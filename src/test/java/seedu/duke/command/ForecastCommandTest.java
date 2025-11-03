package seedu.duke.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.duke.client.ArchivedClientList;
import seedu.duke.client.ClientList;
import seedu.duke.container.LookUpTable;
import seedu.duke.exception.FinanceProPlusException;
import seedu.duke.meeting.MeetingList;
import seedu.duke.policy.PolicyList;
import seedu.duke.task.TaskList;
import seedu.duke.user.UserList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastCommandTest {

    private ForecastCommand forecastCommand;
    private LookUpTable lookUpTable;
    private MeetingList meetingList;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws FinanceProPlusException {
        forecastCommand = new ForecastCommand("meeting");
        meetingList = new MeetingList();
        lookUpTable = new LookUpTable(
            new ClientList(),
            new PolicyList(),
            meetingList,
            new TaskList(),
            new UserList(),
            new ArchivedClientList()
        );
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void execute_validMeetingForecast_executesSuccessfully() throws FinanceProPlusException {
        // Add some meetings to test forecast
        meetingList.addItem("t/Future Meeting c/Client 1 d/31-12-2025 from/14:00");
        outContent.reset();
        
        assertDoesNotThrow(() -> forecastCommand.execute(lookUpTable));
        // The forecast will show meetings within next 7 days
        // Since we're using a future date, it should show "No meetings scheduled"
    }

    @Test
    void execute_invalidSubtype_throwsException() {
        ForecastCommand invalidCommand = new ForecastCommand("client");
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            invalidCommand.execute(lookUpTable);
        });
        assertEquals("Forecast is only available for meetings", exception.getMessage());
    }

    @Test
    void execute_taskSubtype_throwsException() {
        ForecastCommand invalidCommand = new ForecastCommand("task");
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            invalidCommand.execute(lookUpTable);
        });
        assertEquals("Forecast is only available for meetings", exception.getMessage());
    }

    @Test
    void printExecutionMessage_printsCorrectMessage() {
        forecastCommand.printExecutionMessage();
        String output = outContent.toString();
        assertTrue(output.contains("----------------------------------------------------"));
    }
}
