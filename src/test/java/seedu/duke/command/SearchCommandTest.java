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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchCommandTest {

    private SearchCommand searchCommand;
    private LookUpTable lookUpTable;
    private ClientList clientList;
    private PolicyList policyList;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws FinanceProPlusException {
        clientList = new ClientList();
        policyList = new PolicyList();
        lookUpTable = new LookUpTable(
            clientList,
            policyList,
            new MeetingList(),
            new TaskList(),
            new UserList(),
            new ArchivedClientList()
        );
        
        // Add test clients
        clientList.addItem("n/Alice Wong c/98765432 id/S1234567A", policyList);
        clientList.addItem("n/Bob Tan c/87654321 id/G7654321B", policyList);
        
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void execute_existingClient_displaysClient() throws FinanceProPlusException {
        searchCommand = new SearchCommand("client", "S1234567A");
        searchCommand.execute(lookUpTable);
        
        String output = outContent.toString();
        assertTrue(output.contains("Client found:"));
        assertTrue(output.contains("Alice Wong"));
        assertTrue(output.contains("S1234567A"));
    }

    @Test
    void execute_nonExistingClient_displaysNotFound() throws FinanceProPlusException {
        searchCommand = new SearchCommand("client", "S0000000X");
        searchCommand.execute(lookUpTable);
        
        String output = outContent.toString();
        assertTrue(output.contains("No client found with NRIC: S0000000X"));
    }

    @Test
    void execute_emptyArguments_throwsException() {
        searchCommand = new SearchCommand("client", "");
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            searchCommand.execute(lookUpTable);
        });
        assertEquals("Please provide a client NRIC to search for.", exception.getMessage());
    }

    @Test
    void execute_nullArguments_throwsException() {
        searchCommand = new SearchCommand("client", null);
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            searchCommand.execute(lookUpTable);
        });
        assertEquals("Please provide a client NRIC to search for.", exception.getMessage());
    }

    @Test
    void execute_whitespaceArguments_throwsException() {
        searchCommand = new SearchCommand("client", "   ");
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            searchCommand.execute(lookUpTable);
        });
        assertEquals("Please provide a client NRIC to search for.", exception.getMessage());
    }

    @Test
    void execute_argumentsWithWhitespace_findsClient() throws FinanceProPlusException {
        searchCommand = new SearchCommand("client", "  S1234567A  ");
        searchCommand.execute(lookUpTable);
        
        String output = outContent.toString();
        assertTrue(output.contains("Client found:"));
        assertTrue(output.contains("Alice Wong"));
    }

    @Test
    void printExecutionMessage_printsCorrectMessage() {
        searchCommand = new SearchCommand("client", "S1234567A");
        searchCommand.printExecutionMessage();
        String output = outContent.toString();
        assertTrue(output.contains("----------------------------------------------------"));
    }
}
