package PlanetSim.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;

public class DateTimeSpinner extends JSpinner
{

    private static final long          serialVersionUID = 1L;
    private final List<ActionListener> actionListeners  = new ArrayList<ActionListener>();

    private String                     command;

    public DateTimeSpinner(final SpinnerModel model)
    {
        super(model);
    }

    public void setActionCommand(final String command)
    {
        this.command = command;
    }

    public void addActionListener(final ActionListener actionListener)
    {
        this.actionListeners.add(actionListener);
    }

    public void notifyActionListeners(final int code)
    {
        for (final ActionListener actionListener : actionListeners)
        {
            actionListener.actionPerformed(this.getActionEvent(code));
        }
    }

    private ActionEvent getActionEvent(final int code)
    {
        return new ActionEvent(this, DateTimeSpinner.getUniqueId(code), this.command);
    }

    private static int getUniqueId(final int code)
    {
        return (int) Calendar.getInstance().getTimeInMillis() ^ code;
    }

    public String getDateTimeString()
    {
        final Calendar calendar = Calendar.getInstance();
        final Date dateTime = ((SpinnerDateModel) this.getModel()).getDate();
        calendar.setTime(dateTime);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        return new SimpleDateFormat("hh:mm a, MMM dd, yyyy").format(dateTime.getTime());
    }

}
