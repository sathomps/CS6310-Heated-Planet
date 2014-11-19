package PlanetSim.display;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.PauseEvent;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.StopEvent;

public class UserControlPanel extends JPanel
{
    private static final long         serialVersionUID = 1L;

    private static final Dimension    PREFERRED_SIZE   = new Dimension(800, 380);
    private static final Dimension    MIN_SIZE   = new Dimension(800, 350);

    private final SimulationSettings  settings;

    private final Map<JButton, Image> defaultImages    = new HashMap<JButton, Image>();

    private final Map<JButton, Image> selectedImages   = new HashMap<JButton, Image>();

    private SpinnerNumberModel        simulationLength;

    private SpinnerNumberModel        gridSpacingModel;
    private SpinnerNumberModel        timeStepModel;

    private static final String       STATUS_RUN       = "run";
    private static final String       STATUS_PAUSE     = "pause";
    private static final String       STATUS_STOP      = "stop";

    private final EventBus            eventBus;

    private JTextField                tlat;

    private JTextField                blat;

    private JTextField                llong;

    private JTextField                rlong;

    private JComboBox                 simulationName;

    private JTextField                eccentricity;

    private JTextField                tilt;

    private JTextField                precision;

    private JTextField                geoAccuracy;

    private JTextField                temporalAccuracy;

    private SpinnerNumberModel        refreshRateModel;
    
	private MyDateTimeSpinner            startDateTimeSpinner;
	private MyDateTimeSpinner            endDateTimeSpinner;

    public UserControlPanel(final EventBus eventBus, final SimulationSettings settings)
    {
        this.eventBus = eventBus;
        this.settings = settings;
        initLayout();

        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        add(panel);

        addSettingsPanel(panel);

        addlocationPanel(panel);
        
        addTimePanel(panel);
       

        final JPanel config = addConfigPanel();

        addGridSpacing(config);
        addSimulationTimeStep(config);
        addRefreshRate(config);
        addSimLength(config);
        
        add(getOutputOptions());
        add(getSimTimePanel());
        add(getControlButtons());
    }

    private JPanel addConfigPanel()
    {
        final JPanel config = new JPanel();
        config.setLayout(new BoxLayout(config, BoxLayout.X_AXIS));
        config.setBorder(BorderFactory.createTitledBorder("Simulation Settings"));
        add(config);
        return config;
    }

    private void addlocationPanel(final Panel panel)
    {
        final JPanel sPanel2 = new JPanel();
        sPanel2.setLayout(new BoxLayout(sPanel2, BoxLayout.Y_AXIS));
        sPanel2.setBorder(BorderFactory.createTitledBorder("Location"));
        addLocationFields(sPanel2);
        panel.add(sPanel2);
    }
    
    private void addTimePanel(final Panel panel)
    {
        final JPanel sPanel3 = new JPanel();
        sPanel3.setLayout(new BoxLayout(sPanel3, BoxLayout.Y_AXIS));
        sPanel3.setBorder(BorderFactory.createTitledBorder("Time"));
        addTimeFields(sPanel3);
        panel.add(sPanel3);
    }

    private void addSettingsPanel(final Panel sPanel)
    {
        final JPanel sPanel1 = new JPanel();
        sPanel1.setLayout(new BoxLayout(sPanel1, BoxLayout.Y_AXIS));
        sPanel1.setBorder(BorderFactory.createTitledBorder("Properties"));
        addSettingsFields(sPanel1);
        sPanel.add(sPanel1);
    }
    
    public static Calendar getDefaultStartDateTime() {
        Calendar calendar = Calendar.getInstance();
        try {
          calendar.set(2000, 0, 4, 0, 0, 0);
          calendar.set(Calendar.MILLISECOND, 0);
          DateFormat formatter = new SimpleDateFormat(
                  "hh:mm a, MMM dd, yyyy");
          calendar.setTime(formatter.parse(formatter.format(calendar.getTime())));
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return calendar;
      }

      public static Calendar getDefaultEndDateTime() {
        Calendar calendar = getDefaultStartDateTime();
        calendar.setTime(getDefaultStartDateTime().getTime());
        calendar.add(Calendar.MINUTE, 30 * 12 * 24 * 60);
        return calendar;
      }
    
    
    private Component addTimeFields(JPanel datePanel) {
    	
    	TableLayout datePanelLayout = new TableLayout(new double[][] {
    	        { TableLayout.FILL, 50.0, 170.0, TableLayout.FILL },
    	        { 30.0, 30.0, 30.0, 30.0 } });
    	    datePanelLayout.setHGap(10);
    	    datePanelLayout.setVGap(10);
    	    datePanel.setLayout(datePanelLayout);
        {
          JLabel startDateLabel = new JLabel();
          datePanel.add(startDateLabel, "1, 0");
          startDateLabel.setText("Start");
          startDateLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        }
        {
          JLabel endDateLabel = new JLabel();
          datePanel.add(endDateLabel, "1, 1");
          endDateLabel.setText("End");
          endDateLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        }
        {
          SpinnerDateModel startDateTimeModel;
          startDateTimeModel = new SpinnerDateModel(getDefaultStartDateTime().getTime(), null, null,
              Calendar.DAY_OF_MONTH);
          startDateTimeSpinner = new MyDateTimeSpinner(startDateTimeModel);

          JSpinner.DateEditor startDateTimeEditor = new JSpinner.DateEditor(
              startDateTimeSpinner, "hh:mm a, MMM dd, yyyy");
          startDateTimeSpinner.setEditor(startDateTimeEditor);
          startDateTimeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
              startDateTimeSpinner.notifyActionListeners(e.hashCode());
            }

          });
          datePanel.add(startDateTimeSpinner, "2, 0");
        }
        {
          SpinnerDateModel endDateTimeModel;
          endDateTimeModel = new SpinnerDateModel(
              getDefaultEndDateTime().getTime(), null, null, Calendar.DAY_OF_MONTH);
          endDateTimeSpinner = new MyDateTimeSpinner(endDateTimeModel);
          JSpinner.DateEditor endDateTimeEditor = new JSpinner.DateEditor(
              endDateTimeSpinner, "hh:mm a, MMM dd, yyyy");
          endDateTimeSpinner.setEditor(endDateTimeEditor);
          endDateTimeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
              endDateTimeSpinner.notifyActionListeners(e.hashCode());
            }

          });
          datePanel.add(endDateTimeSpinner, "2, 1");
        }
        return datePanel;
      }
    
    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(MIN_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    private Panel getControlButtons()
    {
        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(createButton(STATUS_RUN));
        panel.add(createButton(STATUS_PAUSE));
        panel.add(createButton(STATUS_STOP));
        return panel;
    }

    private JPanel getOutputOptions()
    {
        final JCheckBox max = new JCheckBox("Max");
        final JCheckBox min = new JCheckBox("Min");
        final JCheckBox meanT = new JCheckBox("Mean(Time)");
        final JCheckBox meanR = new JCheckBox("Mean(Region)");
        final JCheckBox showAnimation = new JCheckBox("Show Animation");
        final JPanel outPutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outPutPanel.add(max);
        outPutPanel.add(min);
        outPutPanel.add(meanT);
        outPutPanel.add(meanR);
        outPutPanel.add(showAnimation);
        outPutPanel.setBorder(BorderFactory.createTitledBorder("Output Options"));
        return outPutPanel;
    }

    private JPanel getSimTimePanel()
    {
        final JPanel simTimePanel = new JPanel();
        simTimePanel.setLayout(new BoxLayout(simTimePanel, BoxLayout.X_AXIS));
        simTimePanel.setBorder(BorderFactory.createTitledBorder("Simulation Status"));

        addSimTimeField(simTimePanel, "Simulation Time:");
        addSimTimeField(simTimePanel, "Orbital Position:");
        addSimTimeField(simTimePanel, "Rotational Position:");

        return simTimePanel;
    }

    private void addSimTimeField(final JPanel simTimePanel, final String label)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JLabel value = new JLabel("");
        row.add(new JLabel(label));
        row.add(value);
        simTimePanel.add(row);
    }

    private void addSettingsFields(final JPanel root)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        simulationName = new JComboBox();
        simulationName.setEditable(true);
        row.add(new JLabel("Name:"));
        row.add(simulationName);
        root.add(row);

        addField(root, eccentricity = new JTextField(5), "Eccentricity:");
        addField(root, tilt = new JTextField(5), "Tilt:");
        //addField(root, precision = new JTextField(5), "Precision:");
    }

    private void addLocationFields(final JPanel root)
    {
        addField(root, tlat = new JTextField(5), "Start Latitude:");
        addField(root, blat = new JTextField(5), "End Latitude:");
        addField(root, llong = new JTextField(5), "Start Longitude:");
        addField(root, rlong = new JTextField(5), "End Longitude:");
    }

    private void addField(final JPanel root, final JTextField field, final String label)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(field);
        root.add(row);
    }

    private void addGridSpacing(final JPanel root)
    {
        addSpinner(root, timeStepModel = new SpinnerNumberModel(new Integer(15), new Integer(1), new Integer(180), new Integer(15)), "Grid Spacing");
    }

    private void addSimulationTimeStep(final JPanel root)
    {
        addSpinner(root, timeStepModel = new SpinnerNumberModel(new Integer(1440), new Integer(1), new Integer(525600), new Integer(60)), "Time Step");
    }

    private void addRefreshRate(final JPanel root)
    {
        addSpinner(root, refreshRateModel = new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1)), "Refresh Rate");
    }

    private void addSimLength(final JPanel root)
    {
        addSpinner(root, refreshRateModel = new SpinnerNumberModel(new Integer(12), new Integer(1), new Integer(1200), new Integer(12)), "Simulation Length");
    }

    private void addSpinner(final JPanel root, final SpinnerNumberModel model, final String label)
    {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(new JSpinner(model), BorderLayout.CENTER);

        root.add(panel);
    }

    private JButton createButton(final String buttonID)
    {
        final JButton button = new JButton(buttonID)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String getActionCommand()
            {
                return buttonID;
            };
        };

        setActionListener(button);
        setButtonImage(buttonID, button);
        return button;
    }

    private void setActionListener(final JButton button)
    {
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                final JButton button = (JButton) e.getSource();
                controlSimulation(button);
                resetImage(button);
            }

            private void controlSimulation(final JButton button)
            {
                if (STATUS_RUN.equalsIgnoreCase(button.getActionCommand()))
                {
                    try
                    {
                        eventBus.publish(new RunEvent(settings.clone()));
                    }
                    catch (final CloneNotSupportedException e)
                    {
                    }
                }
                else if (STATUS_STOP.equalsIgnoreCase(button.getActionCommand()))
                {
                    eventBus.publish(new StopEvent());
                }
                else if (STATUS_PAUSE.equalsIgnoreCase(button.getActionCommand()))
                {
                    eventBus.publish(new PauseEvent());
                }
            }

        });
    }

    private void setButtonImage(final String buttonID, final JButton button)
    {
        try
        {
            final Image defaultImg = ImageIO.read(getClass().getResource(buttonID + ".png"));
            defaultImages.put(button, defaultImg);
            final Image selImg = ImageIO.read(getClass().getResource(buttonID + "_sel.png"));
            selectedImages.put(button, selImg);

            button.setIcon(new ImageIcon(defaultImg));
            button.setSize(2, 2);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setText("");
        }
        catch (final IOException ex)
        {
        }
    }

    private void resetImage(final JButton selectedButton)
    {
        selectedButton.setIcon(new ImageIcon(selectedImages.get(selectedButton)));

        for (final JButton button : defaultImages.keySet())
        {
            if (!button.equals(selectedButton))
            {
                button.setIcon(new ImageIcon(defaultImages.get(button)));
            }
        }
    }

    private SimulationSettings cloneSettings(final SimulationSettings settings)
    {
        final SimulationSettings cloneSettings;
        try
        {
            cloneSettings = settings.clone();
            cloneDateSettings(cloneSettings);
            cloneSimulationSettings(cloneSettings);
            cloneLocationSettings(cloneSettings);
            cloneAccuracySettings(cloneSettings);

            cloneSettings.setGridSpacing(gridSpacingModel.getNumber().intValue());
            cloneSettings.setSimulationTimeStepMinutes(timeStepModel.getNumber().intValue());
            cloneSettings.setUIRefreshRate(refreshRateModel.getNumber().intValue());
            return cloneSettings;
        }
        catch (final CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private void cloneAccuracySettings(final SimulationSettings settings)
    {
        settings.setGeographicPrecision(convertTextToInt(geoAccuracy.getText()));
        settings.setTemporalPrecision(convertTextToInt(temporalAccuracy.getText()));
    }

    private void cloneSimulationSettings(final SimulationSettings settings)
    {
        settings.setSimulationName(simulationName.getSelectedItem().toString());
        settings.setPlanetsOrbitalEccentricity(convertTextToInt(eccentricity.getText()));
        settings.setPlanetsAxialTilt(convertTextToInt(tilt.getText()));
        settings.setTemporalPrecision(convertTextToInt(precision.getText()));
    }

    private void cloneDateSettings(final SimulationSettings cloneSettings)
    {
        final Calendar startDate = Calendar.getInstance();
        startDate.setTime(((SpinnerDateModel) startDateTimeSpinner.getModel())
                .getDate());
        cloneSettings.setSimulationStartDate(startDate);

        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(((SpinnerDateModel) endDateTimeSpinner.getModel())
                .getDate());;
        cloneSettings.setSimulationEndDate(endDate);
    }

    private void cloneLocationSettings(final SimulationSettings settings)
    {
        settings.setLatitudeTop(convertTextToInt(tlat.getText()));
        settings.setLatitudeBottom(convertTextToInt(blat.getText()));
        settings.setLongitudeLeft(convertTextToInt(llong.getText()));
        settings.setLongitudeRight(convertTextToInt(rlong.getText()));
    }

    private int convertTextToInt(final String value)
    {
        return ((value != null) && (value.length() > 0)) ? Integer.parseInt(value) : 0;
    }
}
