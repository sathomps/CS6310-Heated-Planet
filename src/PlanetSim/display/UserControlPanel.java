package PlanetSim.display;

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
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Status;

public class UserControlPanel extends JPanel
{
    private static final long         serialVersionUID = 1L;

    private static final Dimension    PREFERRED_SIZE   = new Dimension(800, 400);

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
        
        addSettingsFields();
        addSimTimePanel();
        addResultPanel();
        addControlButtons();
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    private void addControlButtons()
    {
        final Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 3));
        panel.add(createButton("run"));
        panel.add(createButton("pause"));
        panel.add(createButton("stop"));
        add(panel);
    }
    
	private void addSimTimePanel() {
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

		add(simTimePanel);
	}
	
	
	private void addResultPanel() {
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
		add(resultPanel);
	}

    private void addSettingsFields()
    {
        addGridSpacing();
        addSimulationTimeStep();
        addVisRate();
        addSimLength();
    }

    private void addGridSpacing()
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

        add(panel);
    }

    private void addSimulationTimeStep()
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

        add(panel);
    }
    
    private void addVisRate()
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

        add(panel);
    }
    
    private void addSimLength()
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

        add(panel);
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
