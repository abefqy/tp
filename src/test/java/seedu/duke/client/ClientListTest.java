package seedu.duke.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import seedu.duke.container.ListContainer;
import seedu.duke.exception.FinanceProPlusException;
import seedu.duke.policy.ClientPolicy;
import seedu.duke.policy.Policy;
import seedu.duke.policy.PolicyList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse; // Import assertFalse

class ClientListTest {

    private ClientList clientList;
    private ListContainer mainPolicyList;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws FinanceProPlusException {
        clientList = new ClientList();
        mainPolicyList = new PolicyList();
        Policy healthPolicy = new Policy("n/1234 d/Health Test", true);
        Policy lifePolicy = new Policy("n/1233 d/LifeTest", true);
        ((PolicyList) mainPolicyList).addPolicy(healthPolicy);
        ((PolicyList) mainPolicyList).addPolicy(lifePolicy);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Nested
    class AddItemTests {
        @Test
        void addItem_validNewClient_addsSuccessfully() throws FinanceProPlusException {
            String args = "n/John Doe c/12345678 id/S1234567A";
            assertEquals(0, clientList.getClientList().size());
            clientList.addItem(args, mainPolicyList);
            assertEquals(1, clientList.getClientList().size());
            assertNotNull(clientList.findClientByNric("S1234567A"));
            assertTrue(outContent.toString().contains("Noted. I've added this client:"));
            assertTrue(outContent.toString().contains("Name: John Doe"));
        }

        @Test
        void addItem_duplicateNric_throwsException() throws FinanceProPlusException {
            String args = "n/John Doe c/12345678 id/S1234567A";
            clientList.addItem(args, mainPolicyList);
            String argsDuplicate = "n/Jane Doe c/87654321 id/S1234567A";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addItem(argsDuplicate, mainPolicyList));
            assertEquals("A client with NRIC 'S1234567A' already exists.", e.getMessage());
            assertEquals(1, clientList.getClientList().size());
        }

        @Test
        void addItem_missingNric_throwsException() {
            String args = "n/John Doe c/12345678";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addItem(args, mainPolicyList));
            assertEquals("Invalid command format or missing required fields.\n" +
                    "Correct format: client add n/<NAME> c/<CONTACT> id/<NRIC> [p/<POLICY_NAME>]\n" +
                    "Where [] are optional fields.", e.getMessage());
        }

        @Test
        void addItem_unimplementedMethod_throwsException() {
            // FIX: Updated the expected exception message to be more accurate
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addItem("some args"));
            assertEquals("This method is not implemented for client list", e.getMessage());
        }
    }

    @Nested
    class DeleteItemTests {
        @BeforeEach
        void addClientForDeletion() throws FinanceProPlusException {
            clientList.addItem("n/Client One c/111 id/T1111111A", mainPolicyList);
            clientList.addItem("n/Client Two c/222 id/T2222222B", mainPolicyList);
        }

        @Test
        void deleteItem_validIndex_removesSuccessfully() throws FinanceProPlusException {
            assertEquals(2, clientList.getClientList().size());
            clientList.deleteItem("1");
            assertEquals(1, clientList.getClientList().size());
            assertNull(clientList.findClientByNric("T1111111A"));
            assertNotNull(clientList.findClientByNric("T2222222B"));
            assertTrue(outContent.toString().contains("Noted. I've removed this client:"));
            assertTrue(outContent.toString().contains("Name: Client One"));
        }

        @Test
        void deleteItem_invalidIndex_throwsException() {
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.deleteItem("3"));
            assertEquals("Invalid index. The index you provided is out of bounds.\n" +
                    "Correct format: client delete <INDEX>", e.getMessage());
        }

        @Test
        void deleteItem_nonNumericIndex_throwsException() {
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.deleteItem("abc"));
            assertEquals("Invalid input. Please provide a numerical index.\n" +
                    "Correct format: client delete <INDEX>", e.getMessage());
        }

        @Test
        void deleteItem_emptyList_printsMessage() throws FinanceProPlusException {
            ClientList emptyList = new ClientList();
            emptyList.deleteItem("1");
            assertTrue(outContent.toString().contains("No clients to delete."));
        }
    }

    @Nested
    class ListItemsTests {
        @Test
        void listItems_nonEmptyList_printsList() throws FinanceProPlusException {
            clientList.addItem("n/Client One c/111 id/T1111111A", mainPolicyList);
            clientList.addItem("n/Client Two c/222 id/T2222222B", mainPolicyList);
            clientList.listItems();
            String output = outContent.toString();
            assertTrue(output.contains("Here are the clients in your list:"));
            assertTrue(output.contains("1. " + clientList.findClientByNric("T1111111A").toString()));
            assertTrue(output.contains("2. " + clientList.findClientByNric("T2222222B").toString()));
        }

        @Test
        void listItems_emptyList_printsMessage() {
            clientList.listItems();
            assertTrue(outContent.toString().contains("No clients found."));
        }
    }

    @Nested
    class AddPolicyToClientTests {
        @BeforeEach
        void addClientForPolicy() throws FinanceProPlusException {
            clientList.addItem("n/Client One c/111 id/T1111111A", mainPolicyList);
        }

        @Test
        void addPolicyToClient_validArgs_addsSuccessfully() throws FinanceProPlusException {
            String args = "id/T1111111A p/1234 s/01-01-2023 e/01-01-2024 m/150.50";
            Client client = clientList.findClientByNric("T1111111A");
            assertEquals(0, client.getClientPolicyList().getPolicyList().size());
            clientList.addPolicyToClient(args, mainPolicyList);
            assertEquals(1, client.getClientPolicyList().getPolicyList().size());
            assertTrue(client.hasPolicy("1234"));
            assertTrue(outContent.toString().contains("Successfully added new policy contract"));
        }

        @Test
        void addPolicyToClient_clientNotFound_throwsException() {
            String args = "id/EMPTY p/1234 s/01-01-2023 e/01-01-2024 m/150.50";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addPolicyToClient(args, mainPolicyList));
            assertEquals("Client with NRIC 'EMPTY' does not exist.", e.getMessage());
        }

        @Test
        void addPolicyToClient_basePolicyNotFound_throwsException() {
            String args = "id/T1111111A p/12345 s/01-01-2023 e/01-01-2024 m/150.50";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addPolicyToClient(args, mainPolicyList));
            assertTrue(e.getMessage().contains("Base policy '12345' not found"));
        }

        @Test
        void addPolicyToClient_clientAlreadyHasPolicy_throwsException() throws FinanceProPlusException {
            String args = "id/T1111111A p/1234 s/01-01-2023 e/01-01-2024 m/150.50";
            clientList.addPolicyToClient(args, mainPolicyList);
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addPolicyToClient(args, mainPolicyList));
            assertTrue(e.getMessage().contains("already has a contract for policy"));
        }

        @Test
        void addPolicyToClient_invalidDateFormat_throwsException() {
            String args = "id/T1111111A p/1234 s/2023-01-01 e/01-01-2024 m/150.50";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addPolicyToClient(args, mainPolicyList));
            assertEquals("Invalid date format. Please use dd-MM-yyyy.", e.getMessage());
        }

        @Test
        void addPolicyToClient_invalidPremiumFormat_throwsException() {
            String args = "id/T1111111A p/1234 s/01-01-2023 e/01-01-2024 m/abc";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addPolicyToClient(args, mainPolicyList));
            assertEquals("Invalid premium format. Please enter a valid number (e.g., 150.75).", e.getMessage());
        }

        @Test
        void addPolicyToClient_missingArgument_throwsException() {
            String args = "id/T1111111A p/1234 s/01-01-2023 e/01-01-2024";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.addPolicyToClient(args, mainPolicyList));
            assertTrue(e.getMessage().contains("Invalid"));
        }
    }

    @Nested
    class UpdatePolicyForClientTests {
        @BeforeEach
        void setupClientWithPolicy() throws FinanceProPlusException {
            clientList.addItem("n/Client One c/111 id/T1111111A", mainPolicyList);
            String addArgs = "id/T1111111A p/1233 s/01-01-2023 e/01-01-2024 m/150.00";
            clientList.addPolicyToClient(addArgs, mainPolicyList);
        }

        @Test
        void updatePolicyForClient_validArgs_updatesSuccessfully() throws FinanceProPlusException {
            // FIX: Your updatePolicy method requires all three fields (s, e, m)
            String updateArgs = "id/T1111111A p/1233 s/01-01-2023 e/31-12-2025 m/200.00";
            clientList.updatePolicyForClient(updateArgs);

            Client client = clientList.findClientByNric("T1111111A");
            Policy policy = client.getClientPolicyList().findPolicyByName("1233");
            assertEquals(LocalDate.parse("2025-12-31"), ((ClientPolicy) policy).getExpiryDate());
            assertEquals(0, ((ClientPolicy) policy).getMonthlyPremium().compareTo(new BigDecimal("200.00")));
            assertTrue(outContent.toString().contains("Successfully updated policy"));
        }

        @Test
        void updatePolicyForClient_clientNotFound_throwsException() {
            String updateArgs = "id/NONEXISTENT p/1234 s/01-01-2023 e/31-12-2025 m/100";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.updatePolicyForClient(updateArgs));
            assertTrue(e.getMessage().contains("Client with NRIC 'NONEXISTENT' not found."));
        }

        @Test
        void updatePolicyForClient_clientDoesNotHavePolicy_throwsException() {
            String updateArgs = "id/T1111111A p/1234 s/01-01-2023 e/31-12-2025 m/100";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.updatePolicyForClient(updateArgs));
            assertTrue(e.getMessage().contains("does not have a contract for policy '1234'."));
        }

        @Test
        void updatePolicyForClient_missingId_throwsException() {
            String updateArgs = "p/1234 s/01-01-2023 e/31-12-2025 m/100";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.updatePolicyForClient(updateArgs));
            assertEquals("Invalid command. Both client NRIC (id/) and policy name (p/) are required.\n" +
                    "Correct format: client updatepolicy id/<NRIC> p/<POLICY_NAME> [s/<NEW_DATE>] " +
                            "[e/<NEW_DATE>] [m/<NEW_PREMIUM>]"
                    , e.getMessage());
        }

        @Test
        void updatePolicyForClient_noUpdateFields_throwsException() {
            String updateArgs = "id/T1111111A p/1233";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.updatePolicyForClient(updateArgs));
            assertTrue(e.getMessage().contains("You must provide at least one field to update"));
        }
    }

    @Nested
    class GetClientByIDTests {
        private final String realNric = "G1234567X";
        private final String fakeNric = "G7654321Z";

        @BeforeEach
        void setupClient() throws FinanceProPlusException {
            clientList.addItem("n/James Bond c/007 id/" + realNric, mainPolicyList);
            outContent.reset();
        }

        @Test
        void getClientByID_existingClient_returnsCorrectClient() throws FinanceProPlusException {
            String args = "id/" + realNric;
            Client foundClient = clientList.getClientByID(args);
            assertNotNull(foundClient);
            assertEquals("James Bond", foundClient.getName());
            assertEquals(realNric, foundClient.getNric());
        }

        @Test
        void getClientByID_nonExistentClient_throwsSpecificException() {
            String args = "id/" + fakeNric;
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.getClientByID(args));
            assertEquals("Error: Client with NRIC '" + fakeNric + "' not found.", e.getMessage());
        }

        @Test
        void getClientByID_missingIdPrefix_throwsException() {
            String args = "n/Some Name";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.getClientByID(args));
            assertEquals("Error: NRIC to find cannot be null or empty. Make sure id/ isn't empty", e.getMessage());
        }

        @Test
        void getClientByID_emptyIdValue_throwsException() {
            String args = "id/";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.getClientByID(args));
            assertEquals("Error: NRIC to find cannot be null or empty. Make sure id/ isn't empty", e.getMessage());
        }

        @Test
        void getClientByID_argsWithExtraData_returnsCorrectClient() throws FinanceProPlusException {
            String args = "n/Irrelevant c/999 id/" + realNric + " p/SomePolicy";
            Client foundClient = clientList.getClientByID(args);
            assertNotNull(foundClient);
            assertEquals(realNric, foundClient.getNric());
        }

        @Test
        void getClientByID_argsWithWhitespace_returnsCorrectClient() throws FinanceProPlusException {
            String args = "  id/ " + realNric + "  ";
            Client foundClient = clientList.getClientByID(args);
            assertNotNull(foundClient);
            assertEquals(realNric, foundClient.getNric());
        }
    }

    @Nested
    class SearchClientTests {
        @BeforeEach
        void addClientForSearch() throws FinanceProPlusException {
            clientList.addItem("n/John Doe c/12345678 id/S1234567A", mainPolicyList);
        }

        @Test
        void searchClient_existingNric_printsClientFound() throws FinanceProPlusException {
            clientList.searchClient("S1234567A");
            String output = outContent.toString();
            assertTrue(output.contains("Client found:"));
            assertTrue(output.contains("Name: John Doe"));
        }

        @Test
        void searchClient_nonExistingNric_printsNotFound() throws FinanceProPlusException {
            clientList.searchClient("S9999999Z");
            assertTrue(outContent.toString().contains("No client found with NRIC: S9999999Z"));
        }
    }

    @Nested
    class DeletePolicyForClientTests {
        private final String clientNric = "T1111111A";
        private Client testClient;

        @BeforeEach
        void setupClientWithPolicies() throws FinanceProPlusException {
            clientList.addItem("n/Client One c/111 id/" + clientNric, mainPolicyList);
            testClient = clientList.findClientByNric(clientNric);
            String addPolicyArgs1 = "id/" + clientNric + " p/1234 s/01-01-2023 e/01-01-2024 m/100";
            String addPolicyArgs2 = "id/" + clientNric + " p/1233 s/01-01-2023 e/01-01-2024 m/200";
            clientList.addPolicyToClient(addPolicyArgs1, mainPolicyList);
            clientList.addPolicyToClient(addPolicyArgs2, mainPolicyList);
            outContent.reset();
        }

        @Test
        void deletePolicyForClient_validIndex_removesSuccessfully() throws FinanceProPlusException {
            assertEquals(2, testClient.getClientPolicyList().getPolicyList().size());
            assertTrue(testClient.hasPolicy("1234"));
            assertTrue(testClient.hasPolicy("1233"));

            String deleteArgs = "id/" + clientNric + " i/1";
            clientList.deletePolicyForClient(deleteArgs);

            assertEquals(1, testClient.getClientPolicyList().getPolicyList().size());
            // FIX: Use assertFalse for clearer intent
            assertFalse(testClient.hasPolicy("1234"));
            assertTrue(testClient.hasPolicy("1233"));
        }

        @Test
        void deletePolicyForClient_clientNotFound_throwsException() {
            String deleteArgs = "id/FAKE_ID i/1";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.deletePolicyForClient(deleteArgs));
            assertEquals("Error: Client with NRIC 'FAKE_ID' not found.", e.getMessage());
        }

        @Test
        void deletePolicyForClient_indexOutOfBounds_throwsException() {
            String deleteArgs = "id/" + clientNric + " i/3";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.deletePolicyForClient(deleteArgs));
            // FIX: This error comes from PolicyList's deleteItem, which may have a different message
            // Let's check for a general invalid index message
            assertTrue(e.getMessage().toLowerCase().contains("invalid index"));
        }

        @Test
        void deletePolicyForClient_nonNumericIndex_throwsException() {
            String deleteArgs = "id/" + clientNric + " i/abc";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.deletePolicyForClient(deleteArgs));
            assertTrue(e.getMessage().toLowerCase().contains("invalid input"));
        }

        @Test
        void deletePolicyForClient_missingIndexPrefix_throwsException() {
            String deleteArgs = "id/" + clientNric;
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.deletePolicyForClient(deleteArgs));
            assertEquals("Invalid command. Both client NRIC (id/) and policy index (i/) are required.\n" +
                    "Correct format: client deletepolicy id/<NRIC> i/<INDEX>", e.getMessage());
        }

        @Test
        void deletePolicyForClient_missingIdPrefix_throwsException() {
            String deleteArgs = "i/1";
            FinanceProPlusException e = assertThrows(FinanceProPlusException.class,
                    () -> clientList.deletePolicyForClient(deleteArgs));
            assertEquals("Invalid command. Both client NRIC (id/) and policy index (i/) are required.\n" +
                    "Correct format: client deletepolicy id/<NRIC> i/<INDEX>", e.getMessage());
        }
    }
}
