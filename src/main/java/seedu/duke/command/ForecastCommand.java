package seedu.duke.command;

import seedu.duke.container.LookUpTable;
import seedu.duke.exception.FinanceProPlusException;
import seedu.duke.meeting.MeetingList;

public class ForecastCommand extends Command {
    private String arguments;

    /**
     * Creates a ForecastCommand for displaying upcoming meetings.
     *
     * @param subtype The type of forecast (must be "meeting").
     */
    public ForecastCommand(String subtype) {
        this.arguments = subtype;
    }

    /**
     * Executes the forecast command by displaying meetings in the next 7 days.
     *
     * @param lookUpTable The lookup table containing all lists.
     * @throws FinanceProPlusException If the subtype is not "meeting".
     */
    @Override
    public void execute(LookUpTable lookUpTable) throws FinanceProPlusException {
        if (!arguments.equals("meeting")) {
            throw new FinanceProPlusException("Forecast is only available for meetings");
        }
        MeetingList meetings = (MeetingList) lookUpTable.getList("meeting");
        meetings.listForecast();
    }

    @Override
    public void printExecutionMessage() {
        System.out.println("----------------------------------------------------");
    }
}
