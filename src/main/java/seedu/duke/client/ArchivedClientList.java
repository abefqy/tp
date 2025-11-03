package seedu.duke.client;

import seedu.duke.container.ListContainer;
import seedu.duke.exception.FinanceProPlusException;

import java.util.ArrayList;
import java.util.List;

public class ArchivedClientList implements ListContainer {
    private ArrayList<Client> archivedClients;

    public ArchivedClientList() {
        this.archivedClients = new ArrayList<>();
        assert archivedClients != null : "Archived clients list should be initialised properly";
    }

    /**
     * Archives a client by adding them to the archived list.
     *
     * @param client The client to archive.
     */
    public void archiveClient(Client client) {
        assert client != null : "Client to archive cannot be null";
        int oldSize = archivedClients.size();
        archivedClients.add(client);
        assert archivedClients.size() == oldSize + 1 :
                "Archived clients list size should increase by 1 after archiving a client";
        System.out.println("Noted. I've archived this client:");
        System.out.println(client);
    }

    /**
     * Restores a client from the archived list by removing them at the specified index.
     *
     * @param index The index of the client to restore.
     * @return The restored client.
     * @throws FinanceProPlusException If the index is invalid.
     */
    public Client restoreClient(int index) throws FinanceProPlusException {
        if (index < 0 || index >= archivedClients.size()) {
            throw new FinanceProPlusException("Invalid index. Please provide a valid archived client index.");
        }
        int oldSize = archivedClients.size();
        Client restoredClient = archivedClients.remove(index);
        assert archivedClients.size() == oldSize - 1 :
                "Archived clients list size should decrease by 1 after restoring a client";
        return restoredClient;
    }

    @Override
    public void listItems() {
        if (archivedClients.isEmpty()) {
            System.out.println("No archived clients found.");
        } else {
            System.out.println("Here are the archived clients:");
            for (int i = 0; i < archivedClients.size(); i++) {
                System.out.println((i + 1) + ". " + archivedClients.get(i).toString());
            }
        }
    }

    @Override
    public void addItem(String arguments) throws FinanceProPlusException {
        throw new FinanceProPlusException("Cannot add items directly to archived list");
    }

    @Override
    public void addItem(String arguments, ListContainer policyList) throws FinanceProPlusException {
        throw new FinanceProPlusException("Cannot add items directly to archived list");
    }

    @Override
    public void deleteItem(String arguments) throws FinanceProPlusException {
        throw new FinanceProPlusException("Use restore command instead");
    }

    @Override
    public int checkDeleteIndex(String arguments) throws FinanceProPlusException {
        int index;
        try {
            index = Integer.parseInt(arguments) - 1;
            if (index < 0 || index >= archivedClients.size()) {
                throw new FinanceProPlusException("Invalid index. Please provide a valid archived client index.");
            }
        } catch (NumberFormatException e) {
            throw new FinanceProPlusException("Invalid input. Please provide a valid archived client index.");
        }
        return index;
    }

    public ArrayList<Client> getArchivedClients() {
        return archivedClients;
    }

    /**
     * Converts all archived clients to storage format.
     *
     * @return List of strings in storage format.
     */
    public List<String> toStorageFormat() {
        List<String> lines = new ArrayList<>();
        for (Client client : archivedClients) {
            lines.add(client.toStorageString());
        }
        return lines;
    }

    /**
     * Loads archived clients from storage format.
     *
     * @param lines List of strings in storage format.
     * @param policyList The policy list for client validation.
     * @throws FinanceProPlusException If any client data is invalid.
     */
    public void loadFromStorage(List<String> lines, ListContainer policyList) throws FinanceProPlusException {
        if (lines == null || lines.isEmpty()) {
            return;
        }
        for (String line : lines) {
            Client client = new Client(line, policyList);
            archivedClients.add(client);
        }
    }

    /**
     * Converts all archived clients to CSV format.
     *
     * @return List of string arrays for CSV export.
     */
    public List<String[]> toCSVFormat() {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Name", "Contact", "NRIC", "Policies"});
        for (Client client : archivedClients) {
            rows.add(client.toCSVRow());
        }
        return rows;
    }
}
