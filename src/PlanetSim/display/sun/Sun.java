package PlanetSim.display.sun;

import static PlanetSim.common.util.SunPositionUtil.sunGeometricMeanLongitude;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;

public class Sun extends JPanel
{
    private static final long  serialVersionUID = 1L;

    private int                pathLength;
    private final int          lineOffsetY      = 10;
    private final int          dimHeight        = lineOffsetY + 10;

    private final Color        sunColor         = Color.yellow;
    private final int          sunDiameter      = 10;
    private SimulationSettings settings;

    public Sun(final EventBus eventBus, final SimulationSettings settings)
    {
        this.settings = settings;
        eventBus.subscribe(this);
        drawSunPath();
    }

    private void drawSunPath()
    {
        this.pathLength = settings.getPlanetWidth();
        final Dimension dim = new Dimension(pathLength, dimHeight);
        setPreferredSize(dim);
        setMaximumSize(dim);
    }

    /**
     * Overrides the default paint method for this panel.
     */
    @Override
    public void paint(final Graphics g)
    {
        // draw the path
        g.setColor(Color.black);
        g.drawLine(0, lineOffsetY, pathLength, lineOffsetY);

        // draw the sun
        g.setColor(sunColor);
        g.fillOval((int) sunGeometricMeanLongitude(settings.getSimulationTimestamp()), lineOffsetY / 2, sunDiameter, sunDiameter);
    }

    @Subscribe
    public void process(final DisplayEvent displayEvent)
    {
        settings = displayEvent.getSettings();
        if (settings.getDisplaySimulation() == true)
        {
	        repaint();
        }
    }
}
