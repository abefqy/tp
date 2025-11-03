package seedu.duke.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.duke.exception.FinanceProPlusException;
import seedu.duke.policy.PolicyList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchivedClientListTest {

    private ArchivedClientList archivedClientList;
    private Client testClient;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws FinanceProPlusException {
        archivedClientList = new ArchivedClientList();
        PolicyList policyList = new PolicyList();
        testClient = new Client("n/John Doe c/12345678 id/S1234567A", policyList);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void archiveClient_validClient_archivesSuccessfully() throws FinanceProPlusException {
        assertEquals(0, archivedClientList.getArchivedClients().size());
        archivedClientList.archiveClient(testClient);
        assertEquals(1, archivedClientList.getArchivedClients().size());
        assertEquals(testClient, archivedClientList.getArchivedClients().get(0));
        assertTrue(outContent.toString().contains("Noted. I've archived this client:"));
        assertTrue(outContent.toString().contains("John Doe"));
    }

    @Test
    void restoreClient_validIndex_restoresSuccessfully() throws FinanceProPlusException {
        archivedClientList.archiveClient(testClient);
        outContent.reset();
        
        assertEquals(1, archivedClientList.getArchivedClients().size());
        Client restoredClient = archivedClientList.restoreClient(0);
        assertEquals(0, archivedClientList.getArchivedClients().size());
        assertEquals(testClient, restoredClient);
        // restoreClient method doesn't print anything - it just returns the client
        assertEquals("", outContent.toString().trim());
    }

    @Test
    void restoreClient_invalidIndex_throwsException() throws FinanceProPlusException {
        archivedClientList.archiveClient(testClient);
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            archivedClientList.restoreClient(5);
        });
        assertEquals("Invalid index. Please provide a valid archived client index.", exception.getMessage());
    }

    @Test
    void restoreClient_negativeIndex_throwsException() throws FinanceProPlusException {
        archivedClientList.archiveClient(testClient);
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            archivedClientList.restoreClient(-1);
        });
        assertEquals("Invalid index. Please provide a valid archived client index.", exception.getMessage());
    }

    @Test
    void listItems_nonEmptyList_printsList() throws FinanceProPlusException {
        Client client2 = new Client("n/Jane Smith c/87654321 id/S7654321B", new PolicyList());
        archivedClientList.archiveClient(testClient);
        archivedClientList.archiveClient(client2);
        outContent.reset();
        
        archivedClientList.listItems();
        String output = outContent.toString();
        assertTrue(output.contains("Here are the archived clients:"));
        assertTrue(output.contains("1. Name: John Doe"));
        assertTrue(output.contains("2. Name: Jane Smith"));
    }

    @Test
    void listItems_emptyList_printsMessage() throws FinanceProPlusException {
        archivedClientList.listItems();
        assertTrue(outContent.toString().contains("No archived clients found."));
    }

    @Test
    void addItem_singleArgument_throwsException() {
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            archivedClientList.addItem("some args");
        });
        assertEquals("Cannot add items directly to archived list", exception.getMessage());
    }

    @Test
    void addItem_twoArguments_throwsException() {
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            archivedClientList.addItem("some args", new PolicyList());
        });
        assertEquals("Cannot add items directly to archived list", exception.getMessage());
    }

    @Test
    void deleteItem_throwsException() {
        Exception exception = assertThrows(FinanceProPlusException.class, () -> {
            archivedClientList.deleteItem("1");
        });
        assertEquals("Use restore command instead", exception.getMessage());
    }

    @Test
    void checkDeleteIndex_validIndex_returnsIndex() throws FinanceProPlusException {
        archivedClientList.archiveClient(testClient);
        int result = archivedClientList.checkDeleteIndex("1");
        assertEquals(0, result);
    }

    @Test
    void toStorageFormat_returnsCorrectFormat() throws FinanceProPlusException {
        Client client2 = new Client("n/Jane Smith c/87654321 id/S7654321B", new PolicyList());
        archivedClientList.archiveClient(testClient);
        archivedClientList.archiveClient(client2);
        
        List<String> storage = archivedClientList.toStorageFormat();
        assertEquals(2, storage.size());
        assertTrue(storage.get(0).contains("John Doe"));
        assertTrue(storage.get(1).contains("Jane Smith"));
    }

    @Test
    void loadFromStorage_validData_loadsSuccessfully() throws FinanceProPlusException {
        List<String> data = List.of(
            "n/John Doe c/12345678 id/S1234567A",
            "n/Jane Smith c/87654321 id/S7654321B"
        );
        PolicyList policyList = new PolicyList();
        
        archivedClientList.loadFromStorage(data, policyList);
        assertEquals(2, archivedClientList.getArchivedClients().size());
    }



    @Test
    void toCSVFormat_returnsCorrectFormat() throws FinanceProPlusException {
        Client client2 = new Client("n/Jane Smith c/87654321 id/S7654321B", new PolicyList());
        archivedClientList.archiveClient(testClient);
        archivedClientList.archiveClient(client2);
        
        List<String[]> csv = archivedClientList.toCSVFormat();
        assertEquals(3, csv.size()); // Header + 2 clients
        
        // Check header
        String[] header = csv.get(0);
        assertEquals("Name", header[0]);
        assertEquals("Contact", header[1]);
        assertEquals("NRIC", header[2]);
        assertEquals("Policies", header[3]);
    }
}
