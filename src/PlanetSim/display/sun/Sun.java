package PlanetSim.display.sun;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import PlanetSim.common.SimulationSettings;
import PlanetSim.model.GridCell;

public class Sun extends JPanel
{
    private static final long        serialVersionUID      = 1L;

    private int                      pathLength;
    private double                   pixelsPerDegree;
    private final int                lineOffsetY           = 10;
    private final int                dimHeight             = lineOffsetY + 10;

    private final Color              sunColor              = Color.yellow;
    private final int                sunDiameter           = 10;

    private double                   degreePosition        = 180f;
    private int                      pixelPosition;

    private static int               EARTH_TILT            = 45;
    private static int               MINUTES_OF_YEAR       = 60 * 24 * 365;
    private static int               MINUTES_OF_DAY        = 60 * 24;
    private static int               SUN_RADIUS            = 695500;

    private static int               MAJOR_AXIS_LENGTH     = 152500000;
    private static double            SUN_SURFACE_INTENSITY = 1e21 / (4 * Math.PI * Math.pow(SUN_RADIUS, 2));

    public static final int          HEAT_OUTPUT_PER_HOUR  = 4;

    private static double            ORBIT_DEGREE_MINUTE   = 360.0 / 365.0 / 24.0 / 60.0;
    private static double            SPIN_DEGREE_MINUTE    = 360.0 / 24.0 / 60.0;
    private static int               MINUTE_VERNAL_EQUINOX = 115200;

    private double                   longitude;
    private double                   latRadiation;

    private double                   orbitX;
    private double                   orbitY;
    private double                   equinoxAngle;

    private int                      minuteOfYear          = MINUTE_VERNAL_EQUINOX;
    private int                      minuteOfDay           = 0;

    private double                   minorAxisLength;

    private final SimulationSettings settings;

    public Sun(final SimulationSettings settings)
    {
        this.settings = settings;
        settings.setSun(this);
    }

    public void init()
    {
        drawSunPath();
        calculatePosition();
    }

    public void drawSunPath()
    {
        this.pathLength = settings.getEarthWidth();

        pixelsPerDegree = pathLength / 360f;

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

        calculatePosition();
    }

    private void calculatePosition()
    {
        final double pos = pixelsPerDegree * degreePosition;
        pixelPosition = (int) (pos - (sunDiameter / 2));

        minuteOfYear = (minuteOfYear + 1) % MINUTES_OF_YEAR;
        minuteOfDay = (minuteOfDay + 1) % MINUTES_OF_DAY;

        minorAxisLength = Math.sqrt((Math.pow(MAJOR_AXIS_LENGTH, 4) * Math.pow(MAJOR_AXIS_LENGTH, 2)));
        orbitX = MAJOR_AXIS_LENGTH * Math.cos((MINUTE_VERNAL_EQUINOX) * ORBIT_DEGREE_MINUTE);
        orbitY = minorAxisLength * Math.cos((MINUTE_VERNAL_EQUINOX) * ORBIT_DEGREE_MINUTE);
        equinoxAngle = Math.atan(orbitY / orbitX);
    }

    public double calculateRadiationFactor(final GridCell cell)
    {
        final double aveLat = (cell.getLatitudeTop() + cell.getLatitudeBottom()) / 2;
        final double aveLon = (cell.getLongitudeLeft() + cell.getLongitudeRight()) / 2;

        orbitX = MAJOR_AXIS_LENGTH * Math.cos(Math.toRadians((minuteOfYear - MINUTE_VERNAL_EQUINOX) * ORBIT_DEGREE_MINUTE));
        orbitY = minorAxisLength * Math.cos(Math.toRadians((minuteOfYear - MINUTE_VERNAL_EQUINOX) * ORBIT_DEGREE_MINUTE));
        latRadiation = Math.abs(aveLat
                + (EARTH_TILT * Math.cos(Math.toRadians((((SPIN_DEGREE_MINUTE * minuteOfDay) + Math.atan(orbitY / orbitX)) - equinoxAngle) + aveLon))));

        final double lonRadiation = Math.cos(Math.toRadians(aveLon - this.longitude));

        final double distance = Math.sqrt(((orbitX * orbitX) / 2) + ((orbitY * orbitY) / 2));

        if (lonRadiation < 0)
        {
            return 0;
        }
        else
        {
            return latRadiation * lonRadiation * ((SUN_SURFACE_INTENSITY / Math.pow(distance / SUN_RADIUS, 2)) * Math.pow(10, 10));
        }
    }

    private double calculateHeatCoefficient()
    {
        final double heatFactor = settings.getGridSpacing() / 10f;
        return (settings.getGridSpacing() / 12f) * (heatFactor == 0 ? 1 : heatFactor);
    }

    public double calculateSunHeat(final GridCell cell)
    {
        return (calculateHeatCoefficient() * calculateRadiationFactor(cell));
    }

    /**
     * Resets the sun to its default position.
     */
    public void reset()
    {
        degreePosition = 180f;
        pixelPosition = 0;
        calculatePosition();
        paint(this.getGraphics());
    }
}
