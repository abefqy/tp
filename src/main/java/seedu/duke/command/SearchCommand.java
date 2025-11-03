package seedu.duke.command;

import seedu.duke.client.Client;
import seedu.duke.client.ClientList;
import seedu.duke.container.ListContainer;
import seedu.duke.container.LookUpTable;
import seedu.duke.exception.FinanceProPlusException;

public class SearchCommand extends Command {
    private String arguments;

    /**
     * Creates a SearchCommand for searching clients by NRIC.
     *
     * @param subtype The type of item to search.
     * @param arguments The NRIC to search for.
     */
    public SearchCommand(String subtype, String arguments) {
        this.subtype = subtype;
        this.arguments = arguments;
    }

    /**
     * Executes the search command by finding a client with the specified NRIC.
     *
     * @param lookUpTable The lookup table containing all lists.
     * @throws FinanceProPlusException If the NRIC is empty or null.
     */
    @Override
    public void execute(LookUpTable lookUpTable) throws FinanceProPlusException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new FinanceProPlusException("Please provide a client NRIC to search for.");
        }

        ListContainer listContainer = lookUpTable.getList("client");
        ClientList clientList = (ClientList) listContainer;

        Client foundClient = clientList.findClientByNric(arguments.trim());

        if (foundClient != null) {
            System.out.println("Client found:");
            System.out.println(foundClient.toString());
        } else {
            System.out.println("No client found with NRIC: " + arguments.trim());
        }
    }

    @Override
    public void printExecutionMessage() {
        System.out.println("----------------------------------------------------");
    }
}
