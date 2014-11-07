package EarthSim.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import EarthSim.common.SimulationSettings;
import EarthSim.common.event.EventBus;
import EarthSim.common.event.Status;

public class UserControlPanel extends JPanel
{
    private static final long         serialVersionUID = 1L;

    private static final Dimension    PREFERRED_SIZE   = new Dimension(150, 100);

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
        addControlButtons();
        addSettingsFields();
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

    private void addSettingsFields()
    {
        addGridSpacing();
        addSimulationTimeStep();
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
        final Integer value = new Integer(1);
        final Integer min = new Integer(1);
        final Integer max = new Integer(1440);
        final Integer step = new Integer(1);
        timeStepModel = new SpinnerNumberModel(value, min, max, step);
        final JSpinner spinner = new JSpinner(timeStepModel);

        final JLabel label = new JLabel("Time Step");
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
