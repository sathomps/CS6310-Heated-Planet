package PlanetSim.display;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;

public class SimulationUI extends JFrame
{
    private static final long        serialVersionUID = 1L;

    private final SimulationSettings settings;
    private final EventBus           eventBus;

    public SimulationUI(final EventBus eventBus, final SimulationSettings settings)
    {
        this.settings = settings;
        this.eventBus = eventBus;
        init();
    }

    private void init()
    {
        addGridBagLayout();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addGridBagLayout()
    {
        final GridBagLayout gridbag = new GridBagLayout();
        final JPanel panel = new JPanel(gridbag);
        add(panel);
        addUserControlPanel(gridbag, panel);
        addEarthSunPanel(gridbag, panel);
    }

    private void addEarthSunPanel(final GridBagLayout gridbag, final JPanel panel)
    {
        final EarthSunPanel earthSunPanel = new EarthSunPanel(eventBus, settings);
        final GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        //c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(earthSunPanel, c);
        panel.add(earthSunPanel);
    }

    private void addUserControlPanel(final GridBagLayout gridbag, final JPanel panel)
    {
        final UserControlPanel userControlPanel = new UserControlPanel(eventBus, settings);
        final GridBagConstraints c = new GridBagConstraints();

        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        //c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(userControlPanel, c);
        panel.add(userControlPanel);
    }
}
