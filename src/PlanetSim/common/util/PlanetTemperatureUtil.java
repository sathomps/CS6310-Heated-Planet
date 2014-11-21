package PlanetSim.common.util;

import static PlanetSim.common.util.SunPositionUtil.calculateSunPosition;

import java.awt.Color;
import java.util.Calendar;
import java.util.LinkedList;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.model.GridCell;
import PlanetSim.model.SunPosition;

/**
 * http://ccar.colorado.edu/asen5050/projects/projects_2001/benoit/
 * solar_irradiance_on_mars.htm
 * https://esc.fsu.edu/documents/lectures/ECS2005/SolarRadiation.pdf
 * http://www.atmos.washington.edu/2002Q4/211/notes_greenhouse.html
 * 
 * @author sthomps
 *
 */
public final class PlanetTemperatureUtil
{

    /**
     * Stefan-Boltzmann constant (J * K^-4 * m^-2 * s^-1) see: wikipedia
     * Stefan–Boltzmann constant "the total energy radiated per unit surface
     * area of a black body in unit time is proportional to the fourth power of
     * the thermodynamic temperature"
     */
    private static final double STEFAN_BOLTZMANN_CONSTANT = 5.67e-8;

    private PlanetTemperatureUtil()
    {
    }

    public static Color getColor(final double tempInCelcius)
    {
        final Color c = Color.getHSBColor((float) (.666 * tempInCelcius), 1f, 1f);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 120);
    }

    public static double calculatePlanetsAverageTemperature(final GridSettings gridSettings)
    {
        final LinkedList<LinkedList<GridCell>> grid = gridSettings.getGrid();

        double totalTemp = 0;

        for (int x = 0; x < grid.size(); x++)
        {
            final LinkedList<GridCell> cells = grid.get(x);
            for (int y = 0; y < cells.size(); y++)
            {
                totalTemp += cells.get(y).getTemp();
            }
        }

        return totalTemp / (grid.size() * grid.get(0).size());
    }

    public static void calculatePlanetTemperature(final SimulationSettings settings)
    {
        final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();
        for (int row = 0; row < grid.size(); row++)
        {
            for (int cell = 0; cell < grid.get(0).size(); cell++)
            {
                calculatePlanetCellTemperature(grid.get(row).get(cell), settings.getSimulationTimestamp(), settings.getPlanetsOrbitalEccentricity(),
                        settings.getPlanetsAxialTilt());
            }
        }

    }

    private static void calculatePlanetCellTemperature(final GridCell gridCell, final Calendar date, final double planetsOrbitalEccentrity,
            final double planetsAxialTilt)
    {
        final SunPosition sunPosition = calculateSunPosition(gridCell, date, planetsOrbitalEccentrity, planetsAxialTilt);
        final double sunTemp = caclulateEffectiveTemperature(gridCell, sunPosition, date);
        final double initialTemp = gridCell.getTemp();
        final double coolingTemp = 0;// calculateTemperatureDueToCooling(cell);
        final double neighborTemp = calculateNeighborHeat(gridCell) / 2;
        gridCell.setTemp(initialTemp + sunTemp + coolingTemp + neighborTemp);
    }

    /**
     * return Kelvin
     * 
     * @return
     */

    private static double caclulateEffectiveTemperature(final GridCell gridCell, final SunPosition sunPosition, final Calendar date)
    {
        return Math.pow(calculateSolarSolarRadiationIncident(gridCell, sunPosition, date) * STEFAN_BOLTZMANN_CONSTANT, 4);
    }

    /**
     * return W/m2
     * 
     * @return
     */
    private static double calculateSolarSolarRadiationIncident(final GridCell gridCell, final SunPosition sunPosition, final Calendar date)
    {
        return (calculateSolarRadiation(gridCell, sunPosition, date) * (1 - 0.3)) / 4;
    }

    private static double calculateSolarRadiation(final GridCell gridCell, final SunPosition sunPosition, final Calendar date)
    {
        final double sunHeight = sunPosition.getElevation();
        double maximumSolarRadiation;
        if ((sunHeight > 0.0) && (Math.abs(0.25 / sunHeight) < 50.0))
        {
            maximumSolarRadiation = calculateSunCorrection(sunPosition, date) * sunHeight * Math.exp(-0.25 / sunHeight);
        }
        else
        {
            maximumSolarRadiation = 0;
        }
        return maximumSolarRadiation;
    }

    private static double calculateSunCorrection(final SunPosition sunPosition, final Calendar date)
    {
        final double sunConstantePart = Math.cos(2.0 * Math.PI * date.get(Calendar.DAY_OF_MONTH));
        final double sunCorrection = calculateSolarConstant(sunPosition) * (1.0 + (0.033 * sunConstantePart));
        return sunCorrection;
    }

    private static double calculateSolarConstant(final SunPosition sunPosition)
    {
        return 1367 / sunPosition.getDistanceToSun();
    }

    private static double calculateNeighborHeat(final GridCell cell)
    {
        return (cell.getNorthTemp() + cell.getSouthTemp() + cell.getEastTemp() + cell.getWestTemp()) / 4f;
    }
}
