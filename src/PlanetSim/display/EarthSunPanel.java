package PlanetSim.display;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.planet.Planet;
import PlanetSim.display.sun.Sun;

public class EarthSunPanel extends JPanel
{
    private static final long        serialVersionUID = 1l;

    private static final Dimension   PREFERRED_SIZE   = new Dimension(800, 400);

    private Sun                      sun;
    private Planet                   planet;

    private final SimulationSettings settings;
    private final EventBus           eventBus;

    public EarthSunPanel(final EventBus eventBus, final SimulationSettings settings)
    {
        this.eventBus = eventBus;
        this.settings = settings;

        eventBus.subscribe(this);
        initLayout();
        initPlanet();
        initSun();

        addSunAndPlanet();
        repaint();
    }

    private void addSunAndPlanet()
    {
        add(sun);
        add(planet);
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    private void initPlanet()
    {
        planet = new Planet(eventBus, settings);
        planet.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void initSun()
    {
        sun = new Sun(eventBus, settings);
        sun.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    @Subscribe
    public void process(final DisplayEvent displayEvent)
    {
    	 if (displayEvent.getSettings().getDisplaySimulation() == true)
         {
 	        repaint();
         }
    }
}
