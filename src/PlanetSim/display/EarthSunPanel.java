package PlanetSim.display;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.earth.Earth;
import PlanetSim.display.sun.Sun;

public class EarthSunPanel extends JPanel
{
    private static final long        serialVersionUID = 1l;

    private static final Dimension   PREFERRED_SIZE   = new Dimension(800, 400);

    private Sun                      sun;
    private Earth                    earth;

    private final SimulationSettings settings;
    private final EventBus           eventBus;

    public EarthSunPanel(final EventBus eventBus, final SimulationSettings settings)
    {
        this.eventBus = eventBus;
        this.settings = settings;

        eventBus.subscribe(this);
        initLayout();
        initEarth();
        initSun();

        addSunAndEarth();
        drawGrid();
    }

    private void addSunAndEarth()
    {
        add(sun);
        add(earth);
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    private void initEarth()
    {
        earth = new Earth(settings);
        earth.setAlignmentX(Component.LEFT_ALIGNMENT);
        earth.init();
    }

    private void initSun()
    {
        sun = new Sun(settings);
        sun.setAlignmentX(Component.LEFT_ALIGNMENT);
        sun.init();
    }

    private void drawGrid()
    {
        sun.drawSunPath();
        repaint();
    }

    @Subscribe
    public void process(final DisplayEvent displayEvent)
    {
        repaint();
    }
}
