package PlanetSim.display;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
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
import PlanetSim.common.event.Subscribe;
import PlanetSim.model.PlanetPosition;

public class UserControlPanel extends JPanel
{
    private static final long             serialVersionUID   = 1L;

    private static final Dimension        PREFERRED_SIZE     = new Dimension(800, 400);
    private static final Dimension        MIN_SIZE           = new Dimension(800, 400);

    private final SimulationSettings      settings;

    private final Map<JButton, Image>     defaultImages      = new HashMap<JButton, Image>();

    private final Map<JButton, Image>     selectedImages     = new HashMap<JButton, Image>();

    private SpinnerNumberModel            simulationLength;

    private SpinnerNumberModel            gridSpacingModel;
    private SpinnerNumberModel            timeStepModel;

    private static final String           STATUS_RUN         = "run";
    private static final String           STATUS_PAUSE       = "pause";
    private static final String           STATUS_STOP        = "stop";

    private final EventBus                eventBus;

    private JTextField                    tlat;

    private JTextField                    blat;

    private JTextField                    llong;

    private JTextField                    rlong;

    private JComboBox                     simulationName;

    private JTextField                    eccentricity;

    private JTextField                    tilt;

    private SpinnerNumberModel            refreshRateModel;
    private SpinnerNumberModel            simLengthModel;

    private DateTimeSpinner               startDateTimeSpinner;
    private DateTimeSpinner               endDateTimeSpinner;

    private String                        lastButtonPushed   = "";

    private JLabel                        simulationTime;
    private JLabel                        orbitalPosition;
    private JLabel                        rotationalPosition;

    private JLabel                        maxMinMeanTimeMeanRegionTemp;

    private JCheckBox                     showAnimation;

    private JCheckBox                     showMaxTemp;

    private JCheckBox                     showMinTemp;

    private JCheckBox                     showMeanTimeTemp;

    private JCheckBox                     showMeanRegionTemp;

    private static final DateFormat       SDF_DISPLAY        = new SimpleDateFormat("hh:mm a, MMM dd, yyyy");
    private static final SimpleDateFormat SDF                = new SimpleDateFormat("MM/dd/yyyy hh:mm");

    private static final Calendar         DISPLAY_START_DATE = Calendar.getInstance();
    private static final Calendar         DISPLAY_END_DATE   = Calendar.getInstance();

    static
    {
        calculateDefaultStartDateTime();
        calculateDefaultEndDateTime();
    }

    public UserControlPanel(final EventBus eventBus, final SimulationSettings settings)
    {
        this.eventBus = eventBus;
        eventBus.subscribe(this);

        this.settings = settings;

        initLayout();

        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        add(panel);

        addSettingsPanel(panel);

        addlocationPanel(panel);

        addTimePanel(panel);

        addConfigPanels();

        add(addOutputOptionPanel());
        add(addSimTimePanel());
        add(addControlButtonPanel());

        initSettings();
    }

    @Subscribe
    public void display(final DisplayEvent event)
    {
        displayTime(event);
        displayOrbitalPosition(event);
        displayTemperature(event);
    }

    private void addConfigPanels()
    {
        final JPanel config = addConfigPanel();

        addGridSpacingField(config);
        addSimulationTimeStepField(config);
        addRefreshRateField(config);
        addSimLengthField(config);
    }

    private void initSettings()
    {
        tlat.setText(String.valueOf(settings.getLatitudeTop()));
        blat.setText(String.valueOf(settings.getLatitudeBottom()));
        llong.setText(String.valueOf(settings.getLongitudeLeft()));
        rlong.setText(String.valueOf(settings.getLongitudeRight()));
        eccentricity.setText(String.valueOf(settings.getPlanetsOrbitalEccentricity()));
        tilt.setText(String.valueOf(settings.getPlanetsAxialTilt()));
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
        final JPanel lPanel = new JPanel();
        lPanel.setLayout(new BoxLayout(lPanel, BoxLayout.Y_AXIS));
        lPanel.setBorder(BorderFactory.createTitledBorder("Location"));
        addLocationFields(lPanel);
        panel.add(lPanel);
    }

    private void addTimePanel(final Panel panel)
    {
        final JPanel tPanel = new JPanel();
        tPanel.setLayout(new BoxLayout(tPanel, BoxLayout.Y_AXIS));
        tPanel.setBorder(BorderFactory.createTitledBorder("Time"));
        addTimeFields(tPanel);
        panel.add(tPanel);
    }

    private void addSettingsPanel(final Panel panel)
    {
        final JPanel sPanel = new JPanel();
        sPanel.setLayout(new BoxLayout(sPanel, BoxLayout.Y_AXIS));
        sPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
        addSettingsFields(sPanel);
        panel.add(sPanel);
    }

    private void addTimeFields(final JPanel panel)
    {
        final TableLayout datePanelLayout = new TableLayout(new double[][] { { TableLayout.FILL, 50.0, 170.0, TableLayout.FILL }, { 30.0, 30.0, 30.0, 30.0 } });
        datePanelLayout.setHGap(10);
        datePanelLayout.setVGap(10);
        panel.setLayout(datePanelLayout);
        final JLabel startDateLabel = new JLabel();
        panel.add(startDateLabel, "1, 0");
        startDateLabel.setText("Start");
        startDateLabel.setHorizontalAlignment(SwingConstants.TRAILING);

        addStartDateField(panel);

        addEndDate(panel);
    }

    private void addStartDateField(final JPanel datePanel)
    {
        final JLabel endDateLabel = new JLabel();
        datePanel.add(endDateLabel, "1, 1");
        endDateLabel.setText("End");
        endDateLabel.setHorizontalAlignment(SwingConstants.TRAILING);

        SpinnerDateModel startDateTimeModel;
        startDateTimeModel = new SpinnerDateModel(DISPLAY_START_DATE.getTime(), null, null, Calendar.DAY_OF_MONTH);
        startDateTimeSpinner = new DateTimeSpinner(startDateTimeModel);

        final JSpinner.DateEditor startDateTimeEditor = new JSpinner.DateEditor(startDateTimeSpinner, "hh:mm a, MMM dd, yyyy");
        startDateTimeSpinner.setEditor(startDateTimeEditor);
        startDateTimeSpinner.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(final ChangeEvent e)
            {
                startDateTimeSpinner.notifyActionListeners(e.hashCode());
            }

        });
        datePanel.add(startDateTimeSpinner, "2, 0");
    }

    private void addEndDate(final JPanel panel)
    {
        final SpinnerDateModel endDateTimeModel = new SpinnerDateModel(DISPLAY_END_DATE.getTime(), null, null, Calendar.DAY_OF_MONTH);
        endDateTimeSpinner = new DateTimeSpinner(endDateTimeModel);
        final JSpinner.DateEditor endDateTimeEditor = new JSpinner.DateEditor(endDateTimeSpinner, "hh:mm a, MMM dd, yyyy");
        endDateTimeSpinner.setEditor(endDateTimeEditor);
        endDateTimeSpinner.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(final ChangeEvent e)
            {
                endDateTimeSpinner.notifyActionListeners(e.hashCode());
            }
        });
        panel.add(endDateTimeSpinner, "2, 1");
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(MIN_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    private Panel addControlButtonPanel()
    {
        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(createButton(STATUS_RUN));
        panel.add(createButton(STATUS_PAUSE));
        panel.add(createButton(STATUS_STOP));
        return panel;
    }

    private JPanel addOutputOptionPanel()
    {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(showMaxTemp = new JCheckBox("Max", true));
        panel.add(showMinTemp = new JCheckBox("Min", true));
        panel.add(showMeanTimeTemp = new JCheckBox("Mean (Time)", true));
        panel.add(showMeanRegionTemp = new JCheckBox("Mean (Region)", true));
        panel.add(showAnimation = new JCheckBox("Show Animation", true));
        panel.add(maxMinMeanTimeMeanRegionTemp = new JLabel());
        panel.setBorder(BorderFactory.createTitledBorder("Output Options"));
        return panel;
    }

    private JPanel addSimTimePanel()
    {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Status"));

        addSimTimeField(panel, "Simulation Time:", simulationTime = new JLabel());
        addSimTimeField(panel, "Orbital Position:", orbitalPosition = new JLabel());
        addSimTimeField(panel, "Rotational Position:", rotationalPosition = new JLabel());

        return panel;
    }

    private void addSimTimeField(final JPanel panel, final String label, final JLabel value)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(value);
        panel.add(row);
    }

    private void addSettingsFields(final JPanel root)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        simulationName = new JComboBox();
        final String initialName = "Sim" + Calendar.getInstance().getTimeInMillis();
        simulationName.addItem(initialName);
        simulationName.setEditable(true);
        row.add(new JLabel("Name:"));
        row.add(simulationName);
        root.add(row);

        addField(root, eccentricity = new JTextField(5), "Eccentricity:");
        addField(root, tilt = new JTextField(5), "Tilt:");
        // addField(root, precision = new JTextField(5), "Precision:");
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

    private void addGridSpacingField(final JPanel panel)
    {
        addSpinnerField(panel, gridSpacingModel = new SpinnerNumberModel(new Integer(15), new Integer(1), new Integer(180), new Integer(15)), "Grid Spacing");
    }

    private void addSimulationTimeStepField(final JPanel panel)
    {
        addSpinnerField(panel, timeStepModel = new SpinnerNumberModel(new Integer(1440), new Integer(1), new Integer(525600), new Integer(60)), "Time Step");
    }

    private void addRefreshRateField(final JPanel panel)
    {
        addSpinnerField(panel, refreshRateModel = new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1)), "Refresh Rate");
    }

    private void addSimLengthField(final JPanel panel)
    {
        addSpinnerField(panel, simLengthModel = new SpinnerNumberModel(new Integer(12), new Integer(1), new Integer(1200), new Integer(12)),
                "Simulation Length");
    }

    private void addSpinnerField(final JPanel root, final SpinnerNumberModel model, final String label)
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
                if (!lastButtonPushed.equalsIgnoreCase(button.getActionCommand()))
                {
                    if (STATUS_RUN.equalsIgnoreCase(button.getActionCommand()))
                    {
                        eventBus.publish(new RunEvent(cloneSettings()));
                    }
                    else if (STATUS_STOP.equalsIgnoreCase(button.getActionCommand()))
                    {
                        eventBus.publish(new StopEvent());
                        eventBus.publish(new DisplayEvent(cloneSettings()));
                    }
                    else if (STATUS_PAUSE.equalsIgnoreCase(button.getActionCommand()))
                    {
                        eventBus.publish(new PauseEvent());
                    }
                    lastButtonPushed = button.getActionCommand();
                }
            }
        });
    }

    private void setButtonImage(final String buttonID, final JButton button)
    {
        try
        {
            final Image defaultImg = ImageIO.read(getClass().getResource("images/" + buttonID + ".png"));
            defaultImages.put(button, defaultImg);
            final Image selImg = ImageIO.read(getClass().getResource("images/" + buttonID + "_sel.png"));
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

    private SimulationSettings cloneSettings()
    {
        final SimulationSettings cloneSettings = new SimulationSettings();

        cloneDateSettings(cloneSettings);
        cloneSimulationSettings(cloneSettings);
        cloneLocationSettings(cloneSettings);
        cloneCommandLineSettings(settings);

        cloneSettings.setGridSpacing(gridSpacingModel.getNumber().intValue());
        cloneSettings.setSimulationTimeStepMinutes(timeStepModel.getNumber().intValue());
        cloneSettings.setUIRefreshRate(refreshRateModel.getNumber().intValue());
        cloneSettings.setSimulationLength(simLengthModel.getNumber().intValue());

        return cloneSettings;
    }

    private void cloneCommandLineSettings(final SimulationSettings cloneSettings)
    {
        cloneSettings.setTemporalPrecision(settings.getTemporalPrecision());
        cloneSettings.setGeographicPrecision(settings.getGeographicPrecision());
        cloneSettings.setDatastoragePrecision(settings.getDatastoragePrecision());
    }

    private void cloneSimulationSettings(final SimulationSettings cloneSettings)
    {
        cloneSettings.setDisplaySimulation(showAnimation.isSelected());
        cloneSettings.setSimulationName(simulationName.getSelectedItem().toString());
        cloneSettings.setPlanetsOrbitalEccentricity(convertTextToDbl(eccentricity.getText()));
        cloneSettings.setPlanetsAxialTilt(convertTextToDbl(tilt.getText()));
    }

    private void cloneDateSettings(final SimulationSettings cloneSettings)
    {
        final Calendar startDate = Calendar.getInstance();
        startDate.setTime(((SpinnerDateModel) startDateTimeSpinner.getModel()).getDate());
        cloneSettings.setSimulationStartDate(startDate);

        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(((SpinnerDateModel) endDateTimeSpinner.getModel()).getDate());
        cloneSettings.setSimulationEndDate(endDate);
    }

    private void cloneLocationSettings(final SimulationSettings cloneSettings)
    {
        cloneSettings.setLatitudeTop(convertTextToDbl(tlat.getText()));
        cloneSettings.setLatitudeBottom(convertTextToDbl(blat.getText()));
        cloneSettings.setLongitudeLeft(convertTextToDbl(llong.getText()));
    }

    private double convertTextToDbl(final String value)
    {
        return ((value != null) && (value.length() > 0.)) ? Double.parseDouble(value) : 0.;
    }

    private void displayTime(final DisplayEvent event)
    {
        simulationTime.setText(SDF.format(event.getSettings().getSimulationTimestamp().getTime()));
    }

    private void displayTemperature(final DisplayEvent event)
    {
        final StringBuilder temp = new StringBuilder();

        if (showMaxTemp.isSelected())
        {
            temp.append("Max: " + settings.getMaxTemp() + " ");
        }
        if (showMinTemp.isSelected())
        {
            temp.append("Min: " + settings.getMinTemp() + " ");
        }
        if (showMeanTimeTemp.isSelected())
        {
            temp.append("Mean Time: " + settings.getMeanTimeTemp() + " ");
        }
        if (showMeanRegionTemp.isSelected())
        {
            temp.append("Mean Region: " + settings.getMeanRegionTemp() + " ");
        }

        maxMinMeanTimeMeanRegionTemp.setText(temp.toString());
    }

    private void displayOrbitalPosition(final DisplayEvent event)
    {
        final PlanetPosition planetPosition = event.getSettings().getPlanetPosition();

        orbitalPosition.setText(String.format("(%.2f, %.2f)", planetPosition.getHelioLatitude(), planetPosition.getHelioLongitude()));
    }

    private static void calculateDefaultStartDateTime()
    {
        try
        {
            DISPLAY_START_DATE.set(2000, 0, 4, 0, 0, 0);
            DISPLAY_START_DATE.set(Calendar.MILLISECOND, 0);
            DISPLAY_START_DATE.setTime(SDF_DISPLAY.parse(SDF_DISPLAY.format(DISPLAY_START_DATE.getTime())));
        }
        catch (final ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void calculateDefaultEndDateTime()
    {
        DISPLAY_END_DATE.setTime(DISPLAY_START_DATE.getTime());
        DISPLAY_END_DATE.add(Calendar.MINUTE, 30 * 12 * 24 * 60);
    }
}
