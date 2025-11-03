package seedu.duke.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.duke.client.ArchivedClientList;
import seedu.duke.client.Client;
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

class RestoreCommandTest {

    private RestoreCommand restoreCommand;
    private LookUpTable lookUpTable;
    private ClientList clientList;
    private ArchivedClientList archivedClientList;
    private PolicyList policyList;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws FinanceProPlusException {
        restoreCommand = new RestoreCommand("client", "1");
        clientList = new ClientList();
        archivedClientList = new ArchivedClientList();
        policyList = new PolicyList();
        lookUpTable = new LookUpTable(
            clientList,
            policyList,
            new MeetingList(),
            new TaskList(),
            new UserList(),
            archivedClientList
        );
        
        // Add and archive a client for testing
        Client testClient = new Client("n/John Doe c/12345678 id/S1234567A", policyList);
        archivedClientList.archiveClient(testClient);
        
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void execute_validArchivedClient_restoresSuccessfully() throws FinanceProPlusException {
        assertEquals(0, clientList.getClientList().size());
        assertEquals(1, archivedClientList.getArchivedClients().size());
        
        restoreCommand.execute(lookUpTable);
        
        assertEquals(1, clientList.getClientList().size());
        assertEquals(0, archivedClientList.getArchivedClients().size());
        assertTrue(outContent.toString().contains("Successfully restored client from archive."));
    }

    @Test
    void execute_invalidIndex_throwsException() throws FinanceProPlusException {
        RestoreCommand invalidCommand = new RestoreCommand("client", "5");
        
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            invalidCommand.execute(lookUpTable);
        });
        assertEquals("Invalid index. Please provide a valid archived client index.", exception.getMessage());
    }

    @Test
    void execute_emptyArchivedList_printsMessage() throws FinanceProPlusException {
        // Clear the archived list
        archivedClientList.restoreClient(0);
        clientList.getClientList().clear();
        outContent.reset();
        
        restoreCommand.execute(lookUpTable);
        assertTrue(outContent.toString().contains("No archived clients to restore."));
    }

    @Test
    void execute_nonNumericIndex_throwsException() throws FinanceProPlusException {
        RestoreCommand invalidCommand = new RestoreCommand("client", "abc");
        
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            invalidCommand.execute(lookUpTable);
        });
        assertEquals("Invalid input. Please provide a valid archived client index.", exception.getMessage());
    }

    @Test
    void printExecutionMessage_printsCorrectMessage() {
        restoreCommand.printExecutionMessage();
        String output = outContent.toString();
        assertTrue(output.contains("----------------------------------------------------"));
    }
}
