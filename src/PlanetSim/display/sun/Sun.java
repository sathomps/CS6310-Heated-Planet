package PlanetSim.display.sun;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import PlanetSim.common.SimulationSettings;

public class Sun extends JPanel
{
    private static final long        serialVersionUID = 1L;

    private int                      pathLength;
    private final int                lineOffsetY      = 10;
    private final int                dimHeight        = lineOffsetY + 10;

    private final Color              sunColor         = Color.yellow;
    private final int                sunDiameter      = 10;

    private double                   degreePosition   = 180f;
    private int                      pixelPosition;

    private final SimulationSettings settings;

    public Sun(final SimulationSettings settings)
    {
        this.settings = settings;
        settings.setSun(this);
    }

    public void init()
    {
        drawSunPath();
    }

    public void drawSunPath()
    {
        this.pathLength = settings.getEarthWidth();

        final float pixelsPerDegree = pathLength / 360f;

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
        g.fillOval(pixelPosition, lineOffsetY / 2, sunDiameter, sunDiameter);
    }

    public void movePosition(final double rotationalDegree)
    {
        degreePosition -= rotationalDegree;
        if (degreePosition < 0)
        {
            degreePosition += 360;
        }
    }

    /**
     * Resets the sun to its default position.
     */
    public void reset()
    {
        degreePosition = 180f;
        pixelPosition = 0;
        paint(this.getGraphics());
    }
}
