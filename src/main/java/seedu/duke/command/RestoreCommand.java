package seedu.duke.command;

import seedu.duke.client.ArchivedClientList;
import seedu.duke.client.Client;
import seedu.duke.client.ClientList;
import seedu.duke.container.LookUpTable;
import seedu.duke.exception.FinanceProPlusException;

public class RestoreCommand extends Command {
    private String arguments;

    /**
     * Creates a RestoreCommand for restoring archived clients.
     *
     * @param subtype The type of item to restore (must be "client").
     * @param arguments The index of the archived client to restore.
     */
    public RestoreCommand(String subtype, String arguments) {
        assert subtype != null && subtype.equals("client") : "RestoreCommand only supports client subtype";
        this.subtype = subtype;
        this.arguments = arguments;
    }

    /**
     * Executes the restore command by moving a client from the archived list back to the main list.
     *
     * @param lookUpTable The lookup table containing all lists.
     * @throws FinanceProPlusException If the archived client index is invalid.
     */
    @Override
    public void execute(LookUpTable lookUpTable) throws FinanceProPlusException {
        assert lookUpTable != null : "LookUpTable cannot be null";

        ArchivedClientList archivedList = (ArchivedClientList) lookUpTable.getList("archived");
        ClientList clientList = (ClientList) lookUpTable.getList("client");

        if (archivedList.getArchivedClients().isEmpty()) {
            System.out.println("No archived clients to restore.");
            return;
        }

        int index = archivedList.checkDeleteIndex(arguments);
        Client clientToRestore = archivedList.restoreClient(index);
        clientList.addClient(clientToRestore);
        System.out.println("Successfully restored client from archive.");
    }

    @Override
    public void printExecutionMessage() {
        System.out.println("----------------------------------------------------");
    }
}
