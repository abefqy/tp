package seedu.duke.command;

import seedu.duke.client.ArchivedClientList;
import seedu.duke.client.Client;
import seedu.duke.client.ClientList;
import seedu.duke.container.LookUpTable;
import seedu.duke.exception.FinanceProPlusException;

public class ArchiveCommand extends Command {
    private String arguments;

    /**
     * Creates an ArchiveCommand for archiving clients.
     *
     * @param subtype The type of item to archive (must be "client").
     * @param arguments The index of the client to archive.
     */
    public ArchiveCommand(String subtype, String arguments) {
        assert subtype != null && subtype.equals("client") : "ArchiveCommand only supports client subtype";
        this.subtype = subtype;
        this.arguments = arguments;
    }

    /**
     * Executes the archive command by moving a client from the main list to the archived list.
     *
     * @param lookUpTable The lookup table containing all lists.
     * @throws FinanceProPlusException If the client index is invalid.
     */
    @Override
    public void execute(LookUpTable lookUpTable) throws FinanceProPlusException {
        assert lookUpTable != null : "LookUpTable cannot be null";

        ClientList clientList = (ClientList) lookUpTable.getList("client");
        ArchivedClientList archivedList = (ArchivedClientList) lookUpTable.getList("archived");

        if (clientList.getClientList().isEmpty()) {
            System.out.println("No clients to archive.");
            return;
        }

        int index = clientList.checkDeleteIndex(arguments);
        Client clientToArchive = clientList.getClientList().remove(index);
        archivedList.archiveClient(clientToArchive);
    }

    @Override
    public void printExecutionMessage() {
        System.out.println("----------------------------------------------------");
    }
}
