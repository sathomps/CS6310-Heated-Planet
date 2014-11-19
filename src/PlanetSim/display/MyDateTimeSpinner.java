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

public class MyDateTimeSpinner extends JSpinner {

  private List<ActionListener> actionListeners;
  private String               command;

  {
    actionListeners = new ArrayList<ActionListener>();
  }

  public MyDateTimeSpinner() {
    super();
  }

  public MyDateTimeSpinner(SpinnerModel model) {
    super(model);
  }

  public void setActionCommand(String command) {
    this.command = command;
  }

  public void addActionListener(ActionListener actionListener) {
    this.actionListeners.add(actionListener);
  }

  public void notifyActionListeners(int code) {
    for (ActionListener actionListener : actionListeners) {
      actionListener.actionPerformed(this.getActionEvent(code));
    }
  }

  private ActionEvent getActionEvent(int code) {
    return new ActionEvent(this, MyDateTimeSpinner.getUniqueId(code),
        this.command);
  }

  private static int getUniqueId(int code) {
    return (int) Calendar.getInstance().getTimeInMillis() ^ code;
  }

  public String getDateTimeString() {
    Calendar calendar = Calendar.getInstance();
    Date dateTime = ((SpinnerDateModel) this.getModel()).getDate();
    calendar.setTime(dateTime);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.SECOND, 0);
    return new SimpleDateFormat(
            "hh:mm a, MMM dd, yyyy").format(dateTime.getTime());
  }

}
