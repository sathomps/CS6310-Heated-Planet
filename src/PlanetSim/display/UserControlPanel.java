package PlanetSim.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
import javax.swing.SpinnerNumberModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.PauseEvent;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.StopEvent;

public class UserControlPanel extends JPanel
{
    private static final long         serialVersionUID = 1L;

    private static final Dimension    PREFERRED_SIZE   = new Dimension(800, 450);

    private final SimulationSettings  settings;

    private final Map<JButton, Image> defaultImages    = new HashMap<JButton, Image>();

    private final Map<JButton, Image> selectedImages   = new HashMap<JButton, Image>();

    private SpinnerNumberModel        simulationLength;
    private UtilDateModel             fromDateModel;
    private UtilDateModel             toDateModel;

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

        final Panel sPanel3 = new Panel();
        sPanel3.setLayout(new GridLayout(2, 1));
        panel.add(sPanel3);

        addAccuracyPanel(sPanel3);

        addTimePanel(sPanel3);

        final JPanel config = addConfigPanel();

        addGridSpacing(config);
        addSimulationTimeStep(config);
        addRefreshRate(config);
        addSimLength(config);

        final Panel sPanel4 = new Panel();
        sPanel4.setLayout(new GridLayout(1, 2));
        add(sPanel4);
        addSimTimePanel(sPanel4);
        addResultPanel(sPanel4);

        final Panel sPanel5 = new Panel();
        sPanel5.setLayout(new GridLayout(1, 2));
        add(sPanel5);

        addOutputOptions(sPanel5);

        addControlButtons(sPanel5);
    }

    private JPanel addConfigPanel()
    {
        final JPanel config = new JPanel();
        config.setLayout(new BoxLayout(config, BoxLayout.X_AXIS));
        config.setBorder(BorderFactory.createTitledBorder("Config Options"));
        add(config);
        return config;
    }

    private void addTimePanel(final Panel sPanel3)
    {
        final JPanel sPanel32 = new JPanel();
        sPanel32.setLayout(new BoxLayout(sPanel32, BoxLayout.X_AXIS));
        sPanel32.setBorder(BorderFactory.createTitledBorder("Time"));
        addDateButtons(sPanel32);
        sPanel3.add(sPanel32);
    }

    private void addAccuracyPanel(final Panel sPanel3)
    {
        final JPanel sPanel31 = new JPanel();
        sPanel31.setLayout(new BoxLayout(sPanel31, BoxLayout.Y_AXIS));
        sPanel31.setBorder(BorderFactory.createTitledBorder("Accuracy"));
        addAccuracyFields(sPanel31);
        sPanel3.add(sPanel31);
    }

    private void addlocationPanel(final Panel panel)
    {
        final JPanel sPanel2 = new JPanel();
        sPanel2.setLayout(new BoxLayout(sPanel2, BoxLayout.Y_AXIS));
        sPanel2.setBorder(BorderFactory.createTitledBorder("Location"));
        addLocationFields(sPanel2);
        panel.add(sPanel2);
    }

    private void addSettingsPanel(final Panel sPanel)
    {
        final JPanel sPanel1 = new JPanel();
        sPanel1.setLayout(new BoxLayout(sPanel1, BoxLayout.Y_AXIS));
        sPanel1.setBorder(BorderFactory.createTitledBorder("Properties"));
        addSettingsFields(sPanel1);
        sPanel.add(sPanel1);
    }

    private void addDateButtons(final JPanel root)
    {
        final JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addFromDate(root, row1);
        addToDate(root, row1);
    }

    private void addFromDate(final JPanel root, final JPanel row)
    {
        fromDateModel = new UtilDateModel();
        row.add(new JLabel("From:"));
        row.add(createDatePicker(fromDateModel));
        root.add(row);
    }

    private JDatePickerImpl createDatePicker(final UtilDateModel dateModel)
    {
        final JDatePanelImpl datePanel = new JDatePanelImpl(dateModel);
        return new JDatePickerImpl(datePanel);
    }

    private void addToDate(final JPanel root, final JPanel row1)
    {
        final JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toDateModel = new UtilDateModel();
        row1.add(new JLabel("To:     "));
        row1.add(createDatePicker(toDateModel));
        root.add(row2);
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    private void addControlButtons(final Panel root)
    {
        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(createButton(STATUS_RUN));
        panel.add(createButton(STATUS_PAUSE));
        panel.add(createButton(STATUS_STOP));
        root.add(panel);
    }

    private void addOutputOptions(final Panel root)
    {
        final JCheckBox max = new JCheckBox("Max");
        final JCheckBox min = new JCheckBox("Min");
        final JCheckBox meanT = new JCheckBox("Mean(T)");
        final JCheckBox meanR = new JCheckBox("Mean(R)");
        final JCheckBox showAnimation = new JCheckBox("Show Animation");
        final JPanel outPutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outPutPanel.add(max);
        outPutPanel.add(min);
        outPutPanel.add(meanT);
        outPutPanel.add(meanR);
        outPutPanel.add(showAnimation);
        outPutPanel.setBorder(BorderFactory.createTitledBorder("Output Options"));
        root.add(outPutPanel);
    }

    private void addSimTimePanel(final Panel root)
    {
        final JPanel simTimePanel = new JPanel();
        simTimePanel.setLayout(new BoxLayout(simTimePanel, BoxLayout.Y_AXIS));
        simTimePanel.setBorder(BorderFactory.createTitledBorder("Simulation Status"));

        addSimTimeField(simTimePanel, "Simulation Time:");
        addSimTimeField(simTimePanel, "Orbital Position:");
        addSimTimeField(simTimePanel, "Rotational Position:");

        root.add(simTimePanel);
    }

    private void addSimTimeField(final JPanel simTimePanel, final String label)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JLabel value = new JLabel("");
        row.add(new JLabel(label));
        row.add(value);
        simTimePanel.add(row);
    }

    private void addResultPanel(final Panel root)
    {
        final JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        final JLabel lblMaxtemp = new JLabel();
        final JLabel lblMinTemp = new JLabel();
        final JLabel lblMeanOverReg = new JLabel();
        final JLabel lblSource = new JLabel();
        final JLabel lblInter = new JLabel();

        final JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Max Temp:"));
        row1.add(lblMinTemp);
        resultPanel.add(row1);

        final JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Min Temp:"));
        row2.add(lblMaxtemp);
        resultPanel.add(row2);

        final JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Mean Temp(R):"));
        row3.add(lblMeanOverReg);
        resultPanel.add(row3);

        final JLabel lblMeanOverTime = new JLabel();
        final JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row4.add(new JLabel("Mean Temp(T):"));
        row4.add(lblMeanOverTime);
        resultPanel.add(row4);

        final JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row5.add(new JLabel("Source:"));
        row5.add(lblSource);
        resultPanel.add(row5);

        lblInter.setText("");
        final JPanel row6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row6.add(new JLabel("Interpolation(Spatial/Temporal):"));
        row6.add(lblInter);
        resultPanel.add(row6);

        resultPanel.setBorder(BorderFactory.createTitledBorder("Query Results"));
        root.add(resultPanel);
    }

    private void addSettingsFields(final JPanel root)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        simulationName = new JComboBox();
        simulationName.setEditable(true);
        row.add(new JLabel("Simulation Name:"));
        row.add(simulationName);
        root.add(row);

        addField(root, eccentricity = new JTextField(5), "Eccentricity:");
        addField(root, tilt = new JTextField(5), "Tilt:");
        addField(root, precision = new JTextField(5), "Precision:");
    }

    private void addLocationFields(final JPanel root)
    {
        addField(root, tlat = new JTextField(5), "Top Latitude:");
        addField(root, blat = new JTextField(5), "Bottom Latitude:");
        addField(root, llong = new JTextField(5), "Left Longitude:");
        addField(root, rlong = new JTextField(5), "Right Longitude:");
    }

    private void addField(final JPanel root, final JTextField field, final String label)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(field);
        root.add(row);
    }

    private void addAccuracyFields(final JPanel root)
    {
        geoAccuracy = new JTextField(5);
        addAccuracyField(root, geoAccuracy, "Geo. Accuracy(%):");

        temporalAccuracy = new JTextField(5);
        addAccuracyField(root, temporalAccuracy, "Temporal Accuracy(%):");
    }

    private void addAccuracyField(final JPanel root, final JTextField field, final String label)
    {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label));
        row.add(geoAccuracy);
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
            button.setSize(4, 4);
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
        startDate.setTime(fromDateModel.getValue());
        cloneSettings.setSimulationStartDate(startDate);

        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(toDateModel.getValue());
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
