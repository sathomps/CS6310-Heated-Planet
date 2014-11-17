package PlanetSim.display;

import net.sourceforge.jdatepicker.*;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComponent;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Status;

public class UserControlPanel extends JPanel
{
    private static final long         serialVersionUID = 1L;

    private static final Dimension    PREFERRED_SIZE   = new Dimension(800, 450);

    private final SimulationSettings  settings;

    private final Map<JButton, Image> defaultImages    = new HashMap<JButton, Image>();

    private final Map<JButton, Image> selectedImages   = new HashMap<JButton, Image>();

    private SpinnerNumberModel        gridSpacingModel;
    private SpinnerNumberModel        timeStepModel;

    private final EventBus            eventBus;

    public UserControlPanel(final EventBus eventBus, final SimulationSettings settings)
    {
        this.eventBus = eventBus;
        this.settings = settings;
        initLayout();
        
        Panel sPanel = new Panel();
        sPanel.setLayout(new GridLayout(1, 3));
        add(sPanel);
        
        JPanel sPanel1 = new JPanel();
        sPanel1.setLayout(new BoxLayout(sPanel1,BoxLayout.Y_AXIS));
        sPanel1.setBorder(BorderFactory.createTitledBorder("Properties"));
        addSettingsFields(sPanel1);
        sPanel.add(sPanel1);
        
        JPanel sPanel2 = new JPanel();
        sPanel2.setLayout(new BoxLayout(sPanel2,BoxLayout.Y_AXIS));
        sPanel2.setBorder(BorderFactory.createTitledBorder("Location"));
        addLocationFields(sPanel2);
        sPanel.add(sPanel2);
        
        Panel sPanel3 = new Panel();
        sPanel3.setLayout(new GridLayout(2,1));
        sPanel.add(sPanel3);
        
        JPanel sPanel31 = new JPanel();
        sPanel31.setLayout(new BoxLayout(sPanel31,BoxLayout.Y_AXIS));
        sPanel31.setBorder(BorderFactory.createTitledBorder("Accuracy"));
        addAccuracyFields(sPanel31);
        sPanel3.add(sPanel31);
        
        JPanel sPanel32 = new JPanel();
        sPanel32.setLayout(new BoxLayout(sPanel32,BoxLayout.X_AXIS));
        sPanel32.setBorder(BorderFactory.createTitledBorder("Time"));
        addDateButtons(sPanel32);
        sPanel3.add(sPanel32);
        
		JPanel config = new JPanel();
		config.setLayout(new BoxLayout(config,BoxLayout.X_AXIS));
		config.setBorder(BorderFactory.createTitledBorder("Config Options"));
		add(config);
	
        addGridSpacing(config);
        addSimulationTimeStep(config);
        addVisRate(config);
        addSimLength(config);
        
        Panel sPanel4 = new Panel();
        sPanel4.setLayout(new GridLayout(1,2));
        add(sPanel4);
        addSimTimePanel(sPanel4);
        addResultPanel(sPanel4);
        
        Panel sPanel5 = new Panel();
        sPanel5.setLayout(new GridLayout(1,2));
        add(sPanel5);
     
        addOutputOptions(sPanel5);
        
        addControlButtons(sPanel5);
    }
    

    
    private void addDateButtons(JPanel root)
    {
    	JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel simFromTime = new JLabel("From:");
    	UtilDateModel model1 = new UtilDateModel();
    	JDatePanelImpl datePanel1 = new JDatePanelImpl(model1);
    	JDatePickerImpl datePicker1 = new JDatePickerImpl(datePanel1);
    	row1.add(simFromTime);
    	row1.add(datePicker1);
    	root.add(row1);
    	
    	JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel simToTime = new JLabel("To:     ");
    	UtilDateModel model2 = new UtilDateModel();
    	JDatePanelImpl datePanel2 = new JDatePanelImpl(model2);
    	JDatePickerImpl datePicker2 = new JDatePickerImpl(datePanel2);
    	row1.add(simToTime);
    	row1.add(datePicker2);
    	root.add(row2);
    	
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    @SuppressWarnings("deprecation")
	private void addControlButtons(Panel root)
    {
        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(createButton("run"));
        panel.add(createButton("pause"));
        panel.add(createButton("stop"));
        root.add(panel);
    }
    
    
    @SuppressWarnings("deprecation")
   	private void addOutputOptions(Panel root)
       {
    		JCheckBox max = new JCheckBox("Max");
    		JCheckBox min = new JCheckBox("Min");
    		JCheckBox meanT = new JCheckBox("Mean(T)");
    		JCheckBox meanR = new JCheckBox("Mean(R)");
    		JCheckBox showAnimation = new JCheckBox("Show Animation");
			JPanel outPutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			outPutPanel.add(max);
			outPutPanel.add(min);
			outPutPanel.add(meanT);
			outPutPanel.add(meanR);
			outPutPanel.add(showAnimation);
			outPutPanel.setBorder(BorderFactory.createTitledBorder("Output Options"));
			root.add(outPutPanel);
       }
    
	private void addSimTimePanel(Panel root) {
		JPanel simTimePanel = new JPanel();
		simTimePanel.setLayout(new BoxLayout(simTimePanel,BoxLayout.Y_AXIS));
		simTimePanel.setBorder(BorderFactory.createTitledBorder("Simulation Status"));

		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel simTime = new JLabel("");
		row1.add(new JLabel("Simulation Time:"));row1.add(simTime);
		simTimePanel.add(row1);

		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel simOrbitAngle = new JLabel("");
		row2.add(new JLabel("Orbital Position:"));row2.add(simOrbitAngle);
		simTimePanel.add(row2);

		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel simRotPosition = new JLabel("");
		row3.add(new JLabel("Rotational Position:"));row3.add(simRotPosition);
		simTimePanel.add(row3);

		root.add(simTimePanel);
	}
	
	
	private void addResultPanel(Panel root) {
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));

		JLabel lblMaxtemp = new JLabel();
		JLabel lblMinTemp = new JLabel();
		JLabel lblMeanOverReg = new JLabel();
		JLabel lblSource = new JLabel();
		JLabel lblInter = new JLabel();

		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row1.add(new JLabel("Max Temp:"));row1.add(lblMinTemp);
		resultPanel.add(row1);

		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row2.add(new JLabel("Min Temp:"));row2.add(lblMaxtemp);
		resultPanel.add(row2);

		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row3.add(new JLabel("Mean Temp(R):"));row3.add(lblMeanOverReg);
		resultPanel.add(row3);

		JLabel lblMeanOverTime = new JLabel();
		JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row4.add(new JLabel("Mean Temp(T):"));row4.add(lblMeanOverTime);
		resultPanel.add(row4);
		
		JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row5.add(new JLabel("Source:"));row5.add(lblSource);
		resultPanel.add(row5);
		
		lblInter.setText("");
		JPanel row6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row6.add(new JLabel("Interpolation(Spatial/Temporal):"));row6.add(lblInter);
		resultPanel.add(row6);

		resultPanel.setBorder(BorderFactory.createTitledBorder("Query Results"));
		root.add(resultPanel);
	}

    private void addSettingsFields(JPanel root)
    {
    	JPanel row0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	JComboBox simulationName = new JComboBox();
    	simulationName.setEditable(true);
    	row0.add(new JLabel("Simulation Name:"));
    	row0.add(simulationName);
    	root.add(row0);
    	
		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row1.add(new JLabel("Eccentricity:"));
		JTextField eccentricity = new JTextField(5);
		row1.add(eccentricity);
		root.add(row1);

		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row2.add(new JLabel("Tilt:"));
		JTextField tilt = new JTextField(5);
		row2.add(tilt);
		root.add(row2);

		JPanel row6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row6.add(new JLabel("Precision:"));
		JTextField precision = new JTextField(5);
		row6.add(precision);
		root.add(row6);
    }
    
    
    private void addLocationFields(JPanel root)
    {

		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row1.add(new JLabel("Top Latitude:"));
		JTextField tlat = new JTextField(5);
		row1.add(tlat);
		root.add(row1);
		
		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row2.add(new JLabel("Bottom Latitude:"));
		JTextField blat = new JTextField(5);
		row2.add(blat);
		root.add(row2);
		
		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row3.add(new JLabel("Left Longitude:"));
		JTextField llong = new JTextField(5);
		row3.add(llong);
		root.add(row3);
		
		JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row4.add(new JLabel("Right Longitude:"));
		JTextField rlong = new JTextField(5);
		row4.add(rlong);
		root.add(row4);
    }
    
    private void addAccuracyFields(JPanel root)
    {
    	JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row1.add(new JLabel("Geo. Accuracy(%):"));
		JTextField geoAccuracy = new JTextField(5);
		row1.add(geoAccuracy);
		root.add(row1);
		
    	JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row2.add(new JLabel("Time Accuracy(%):"));
		JTextField timeAccuracy = new JTextField(5);
		row2.add(timeAccuracy);
		root.add(row2);
    }

    private void addGridSpacing(JPanel root)
    {
        final Integer value = new Integer(15);
        final Integer min = new Integer(1);
        final Integer max = new Integer(180);
        final Integer step = new Integer(15);
        gridSpacingModel = new SpinnerNumberModel(value, min, max, step);
        final JSpinner spinner = new JSpinner(gridSpacingModel);

        final JLabel label = new JLabel("Grid Spacing");
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(spinner, BorderLayout.CENTER);

        root.add(panel);
    }

    private void addSimulationTimeStep(JPanel root)
    {
        final Integer value = new Integer(1440);
        final Integer min = new Integer(1);
        final Integer max = new Integer(525600);
        final Integer step = new Integer(60);
        timeStepModel = new SpinnerNumberModel(value, min, max, step);
        final JSpinner spinner = new JSpinner(timeStepModel);

        final JLabel label = new JLabel("Time Step");
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(spinner, BorderLayout.CENTER);

        root.add(panel);
    }
    
    private void addVisRate(JPanel root)
    {
        final Integer value = new Integer(1);
        final Integer min = new Integer(1);
        final Integer max = new Integer(100);
        final Integer step = new Integer(1);
        SpinnerNumberModel refreshRate = new SpinnerNumberModel(value, min, max, step);
        final JSpinner spinner = new JSpinner(refreshRate);

        final JLabel label = new JLabel("Refresh Rate");
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(spinner, BorderLayout.CENTER);

        root.add(panel);
    }
    
    private void addSimLength(JPanel root)
    {
        final Integer value = new Integer(12);
        final Integer min = new Integer(1);
        final Integer max = new Integer(1200);
        final Integer step = new Integer(12);
        SpinnerNumberModel simulationLength = new SpinnerNumberModel(value, min, max, step);
        final JSpinner spinner = new JSpinner(simulationLength);

        final JLabel label = new JLabel("Simulation Length");
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(spinner, BorderLayout.CENTER);

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

        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                final JButton button = (JButton) e.getSource();
                setSettings(button);
                resetImage(button);
            }

            private void setSettings(final JButton button)
            {
                final Status currentStatus = Status.getStatus(button.getActionCommand());
                eventBus.publish(currentStatus);
            }
        });

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
        return button;
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
}
