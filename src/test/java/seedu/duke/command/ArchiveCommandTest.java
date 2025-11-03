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

class ArchiveCommandTest {

    private ArchiveCommand archiveCommand;
    private LookUpTable lookUpTable;
    private ClientList clientList;
    private ArchivedClientList archivedClientList;
    private PolicyList policyList;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws FinanceProPlusException {
        archiveCommand = new ArchiveCommand("client", "1");
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
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void execute_validClient_archivesSuccessfully() throws FinanceProPlusException {
        // Add a client to archive
        clientList.addItem("n/John Doe c/12345678 id/S1234567A", policyList);
        outContent.reset();
        
        assertEquals(1, clientList.getClientList().size());
        assertEquals(0, archivedClientList.getArchivedClients().size());
        
        archiveCommand.execute(lookUpTable);
        
        assertEquals(0, clientList.getClientList().size());
        assertEquals(1, archivedClientList.getArchivedClients().size());
        assertTrue(outContent.toString().contains("Noted. I've archived this client:"));
    }

    @Test
    void execute_invalidIndex_throwsException() throws FinanceProPlusException {
        clientList.addItem("n/John Doe c/12345678 id/S1234567A", policyList);
        ArchiveCommand invalidCommand = new ArchiveCommand("client", "5");
        
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            invalidCommand.execute(lookUpTable);
        });
        assertTrue(exception.getMessage().contains("Invalid index"));
    }

    @Test
    void execute_emptyClientList_printsMessage() throws FinanceProPlusException {
        archiveCommand.execute(lookUpTable);
        assertTrue(outContent.toString().contains("No clients to archive."));
    }

    @Test
    void execute_nonNumericIndex_throwsException() throws FinanceProPlusException {
        clientList.addItem("n/John Doe c/12345678 id/S1234567A", policyList);
        ArchiveCommand invalidCommand = new ArchiveCommand("client", "abc");
        
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            invalidCommand.execute(lookUpTable);
        });
        assertTrue(exception.getMessage().contains("Invalid input"));
    }

    @Test
    void printExecutionMessage_printsCorrectMessage() {
        archiveCommand.printExecutionMessage();
        String output = outContent.toString();
        assertTrue(output.contains("----------------------------------------------------"));
    }
}
