package EarthSim.display;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import EarthSim.common.SimulationSettings;

public class SimulationUI extends JFrame
{
    private static final long        serialVersionUID = 1L;

    private final SimulationSettings settings;

    public SimulationUI(final SimulationSettings settings)
    {
        this.settings = settings;
        init();
    }

    private void init()
    {
        addPanels();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addPanels()
    {
        final JPanel panel = new JPanel(new GridLayout(2, 1));
        add(panel);

        panel.add(new EarthSunPanel(settings));
        panel.add(new UserControlPanel(settings));
    }
}
