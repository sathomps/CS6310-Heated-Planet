package PlanetSim.common;

import static PlanetSim.common.util.PlanetPositionUtil.calculatePlanetPosition;
import static PlanetSim.db.DataSource.SIMULATION;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import PlanetSim.db.DataSource;
import PlanetSim.model.GridCell;
import PlanetSim.model.PlanetPosition;

public class SimulationSettings
{
    private static final int              PLANET_WIDTH              = 800;
    private static final int              PLANET_HEIGHT             = 400;

    private static int                    MINUTES_IN_A_MONTH        = 43829;

    // the next four attributes are the bounding rectangle for the query engine.
    // only the cells within this box are
    // returned/interpolated/simulated/persisted
    private double                        latitudeTop               = -90.;
    private double                        latitudeBottom            = 90.;
    private double                        longitudeLeft             = -180.;
    private double                        longitudeRight            = 180.;

    private String                        simulationName            = "";

    private double                        planetsAxialTilt          = 23.44;

    // //Orbital eccentricity: non-negative real number less than one; default
    // is .0167.
    private double                        planetsOrbitalEccentrity  = 0.0167;

    // Simulation length: non-negative integer (Solar) months between 1 and
    // 1200; default 12 (one Solar year).
    private int                           simulationLength          = 12;

    // -p #: The precision of the data to be stored, in decimal digits after the
    // decimal point. The default is to use the
    // number of digits storable in a normalized float variable. The maximum is
    // the number of digits storable in a
    // normalized double variable. The minimum is zero.
    private int                           datastoragePrecision      = 7;

    // -g #: The geographic precision (sampling rate) of the temperature data to
    // be stored, as an integer percentage of the
    // number of grid cells saved versus the number simulated. The default is
    // 100%; that is, a value is stored for each grid cell.
    private int                           geographicPrecision       = 100;

    // -t #: The temporal precision of the temperature data to be stored, as an
    // integer percentage of the number of time periods
    // saved versus the number computed. The default is 100%; that is, all
    // computed values should be stored.
    private int                           temporalPrecision         = 100;

    private int                           simulationTimeStepMinutes = 1;
    private int                           gridSpacing               = 15;

    private final Calendar                simulationTimestamp;
    private Calendar                      simulationStartDate;
    private Calendar                      simulationEndDate;

    private int                           simulationTimeMinutes     = 1440;

    private GridSettings                  gridSettings;

    private int                           uiRfreshRate              = 1;

    private PlanetPosition                planetPosition;

    private static final SimpleDateFormat SDF                       = new SimpleDateFormat("hh:mm:ss.SSS");

    private DataSource                    dataSource                = SIMULATION;

    public SimulationSettings()
    {
        simulationTimestamp = Calendar.getInstance();
        simulationTimestamp.set(MONTH, 1);
        simulationTimestamp.set(DAY_OF_MONTH, 4);
        createGrid();
        calculatePlanetPosition(this);
    }

    public LinkedList<LinkedList<GridCell>> getGrid()
    {
        return gridSettings.getGrid();
    }

    public int getSimulationTimeStepMinutes()
    {
        return simulationTimeStepMinutes;
    }

    public SimulationSettings setSimulationTimeStepMinutes(final int simulationTimeStepMinutes)
    {
        this.simulationTimeStepMinutes = simulationTimeStepMinutes;
        validateSimulationTimeStepMinutes();
        return this;
    }

    private void validateSimulationTimeStepMinutes()
    {
        simulationTimeStepMinutes = ((simulationTimeStepMinutes >= 1) && (simulationTimeStepMinutes <= 525600)) ? simulationTimeStepMinutes : 1;
    }

    public int getGridSpacing()
    {
        return gridSpacing;
    }

    public SimulationSettings setGridSpacing(final int gridSpacing)
    {
        this.gridSpacing = gridSpacing;
        validateGridSpacing();
        return this;
    }

    private void validateGridSpacing()
    {
        gridSpacing = ((gridSpacing >= 1) && (gridSpacing <= 180)) ? gridSpacing : 15;
        while ((180 % gridSpacing) != 0)
        {
            --gridSpacing;
        }
        createGrid();
    }

    private void createGrid()
    {
        gridSettings = new GridSettings(this);

        final int numCellsY = 180 / getGridSpacing();

        for (int row = 0; row < getNumCellsX(); row++)
        {
            for (int col = 0; col < numCellsY; col++)
            {
                gridSettings.addCell(row, col);
            }
        }
    }

    public int getPixelsPerCellX()
    {
        return getPlanetWidth() / getNumCellsX();
    }

    public int getNumCellsX()
    {
        return 360 / getGridSpacing();
    }

    public int getPlanetWidth()
    {
        return PLANET_WIDTH;
    }

    public int getPlanetHeight()
    {
        return PLANET_HEIGHT;
    }

    public int getPlanetRadius()
    {
        return getPlanetHeight() / 2;
    }

    public String getSimulationName()
    {
        return simulationName;
    }

    public SimulationSettings setSimulationName(final String simulationName)
    {
        this.simulationName = simulationName;
        validateSimulationName();
        return this;
    }

    private void validateSimulationName()
    {
        simulationName = ((simulationName != null) && (simulationName.length() > 0)) ? simulationName : SDF.format(new Date());
    }

    public double getPlanetsAxialTilt()
    {
        return planetsAxialTilt;
    }

    public SimulationSettings setPlanetsAxialTilt(final double planetsAxialTilt)
    {
        this.planetsAxialTilt = planetsAxialTilt;
        validatePlanetsAxialTilt();
        return this;
    }

    private void validatePlanetsAxialTilt()
    {
        planetsAxialTilt = ((planetsAxialTilt >= 1) && (planetsAxialTilt <= 180)) ? planetsAxialTilt : 23.44;
    }

    public double getPlanetsOrbitalEccentricity()
    {
        return planetsOrbitalEccentrity;
    }

    public SimulationSettings setPlanetsOrbitalEccentricity(final double planetsOrbitalEccentrity)
    {
        this.planetsOrbitalEccentrity = planetsOrbitalEccentrity;
        validatPlanetsOrbitalEccentricity();
        return this;
    }

    private void validatPlanetsOrbitalEccentricity()
    {
        planetsOrbitalEccentrity = ((planetsOrbitalEccentrity >= 0) && (planetsOrbitalEccentrity <= 1)) ? planetsOrbitalEccentrity : .0167;
    }

    public int getSimulationLength()
    {
        return simulationLength;
    }

    public SimulationSettings setSimulationLength(final int simulationLength)
    {
        this.simulationLength = simulationLength;
        validateSimulationLength();
        return this;
    }

    private void validateSimulationLength()
    {
        simulationLength = ((simulationLength >= 1) && (simulationLength <= 1200)) ? simulationLength : 12;
    }

    public int getDatastoragePrecision()
    {
        return datastoragePrecision;
    }

    public SimulationSettings setDatastoragePrecision(final int datastoragePrecision)
    {
        this.datastoragePrecision = datastoragePrecision;
        validateDatastoragePrecision();
        return this;
    }

    private void validateDatastoragePrecision()
    {
        datastoragePrecision = ((datastoragePrecision >= 0) && (datastoragePrecision <= 7)) ? datastoragePrecision : 7;
    }

    public int getGeographicPrecision()
    {
        return geographicPrecision;
    }

    public SimulationSettings setGeographicPrecision(final int geographicPrecision)
    {
        this.geographicPrecision = geographicPrecision;
        validateGeographicPrecision();
        return this;
    }

    private void validateGeographicPrecision()
    {
        geographicPrecision = ((geographicPrecision >= 1) && (geographicPrecision <= 100) ? geographicPrecision : 100);
    }

    public int getTemporalPrecision()
    {
        return temporalPrecision;
    }

    public SimulationSettings setTemporalPrecision(final int temporalPrecision)
    {
        this.temporalPrecision = temporalPrecision;
        validaTemporalPrecision();
        return this;
    }

    private void validaTemporalPrecision()
    {
        temporalPrecision = ((temporalPrecision >= 1) && (temporalPrecision <= 100) ? temporalPrecision : 100);
    }

    public GridSettings getGridSettings()
    {
        return gridSettings;
    }

    public double getLatitudeTop()
    {
        return latitudeTop;
    }

    public void setLatitudeTop(final double latitudeTop)
    {
        this.latitudeTop = latitudeTop;
    }

    public double getLatitudeBottom()
    {
        return latitudeBottom;
    }

    public void setLatitudeBottom(final double latitudeBottom)
    {
        this.latitudeBottom = latitudeBottom;
    }

    public double getLongitudeLeft()
    {
        return longitudeLeft;
    }

    public void setLongitudeLeft(final double longitudeLeft)
    {
        this.longitudeLeft = longitudeLeft;
    }

    public double getLongitudeRight()
    {
        return longitudeRight;
    }

    public void setLongitudeRight(final double longitudeRight)
    {
        this.longitudeRight = longitudeRight;
    }

    public void calculateSimulationTimestamp()
    {
        simulationTimeMinutes += getSimulationTimeStepMinutes();
        simulationTimestamp.add(MINUTE, getSimulationTimeStepMinutes());
    }

    public boolean hasSimulationFinished()
    {
        return (calculateMonthsPassed() >= getSimulationLength());
    }

    private int calculateMonthsPassed()
    {
        return simulationTimeMinutes != 0 ? (simulationTimeMinutes / MINUTES_IN_A_MONTH) : 0;
    }

    public Calendar getSimulationTimestamp()
    {
        return simulationTimestamp;
    }

    public Calendar getSimulationStartDate()
    {
        return simulationStartDate;
    }

    public void setSimulationStartDate(final Calendar simulationStartDate)
    {
        this.simulationStartDate = simulationStartDate;
    }

    public Calendar getSimulationEndDate()
    {
        return simulationEndDate;
    }

    public void setSimulationEndDate(final Calendar simulationEndDate)
    {
        this.simulationEndDate = simulationEndDate;
    }

    public void setUIRefreshRate(final int uiRefreshRate)
    {
        this.uiRfreshRate = uiRefreshRate;
    }

    public int getUIRefreshRate()
    {
        return uiRfreshRate;
    }

    public void setGridSettings(final GridSettings gridSettings)
    {
        this.gridSettings = gridSettings;
    }

    public GridSettings getGridSettings(final GridSettings gridSettings)
    {
        return gridSettings;
    }

    public PlanetPosition getPlanetPosition()
    {
        return planetPosition;
    }

    public void setPlanetPosition(final PlanetPosition planetPosition)
    {
        this.planetPosition = planetPosition;
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(final DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public int getGridCellSampleInterval()
    {
        return (int) (geographicPrecision * .1);
    }

    public int getTimePeriodSampleInterval()
    {
        return (int) (temporalPrecision * .1);
    }
}
