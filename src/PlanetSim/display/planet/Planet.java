package PlanetSim.display.planet;

import static PlanetSim.common.GeoUtil.calculateDistanceToEquator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.model.GridCell;

public class Planet extends JPanel
{
    private static final long        serialVersionUID = 1L;

    private int                      pixelsPerCellX;
    private int                      pixelsPerCellY;
    private int                      imgWidth;
    private int                      imgHeight;
    private int                      numCellsX;
    private int                      numCellsY;
    private int                      radius;
    private final SimulationSettings settings;
    private GridSettings             gridSettings;
    private final EarthImage         image            = new EarthImage();

    /**
     * Constructs a display grid with a default grid spacing.
     * 
     * @param settings
     */
    public Planet(final SimulationSettings settings)
    {
        this.settings = settings;
        settings.setPlanet(this);
    }

    public void init()
    {
        gridSettings = new GridSettings(settings);
        calculateGridGranularity();
        setIgnoreRepaint(true);
        createGrid();
    }

    private void calculateGridGranularity()
    {
        numCellsX = 360 / settings.getGridSpacing();
        pixelsPerCellX = image.getIconWidth() / numCellsX;
        imgWidth = numCellsX * pixelsPerCellX;

        numCellsY = 180 / settings.getGridSpacing();
        pixelsPerCellY = image.getIconHeight() / numCellsY;
        imgHeight = numCellsY * pixelsPerCellY;
        radius = imgHeight / 2;

        gridSettings.setHeight(imgHeight).setWidth(imgWidth);
    }

    @Override
    public void paint(final Graphics g)
    {
        drawEarthImage(g);
        fillCellColors(g);
        drawGrid(g);
    }

    @Override
    public int getWidth()
    {
        return imgWidth;
    }

    private void createGrid()
    {
        for (int row = 0; row < numCellsX; row++)
        {
            for (int col = 0; col < numCellsY; col++)
            {
                gridSettings.addCell(row, col, pixelsPerCellX);
            }
        }
    }

    private void fillCellColors(final Graphics g)
    {
        final LinkedList<LinkedList<GridCell>> grid = gridSettings.getGrid();

        int cellX = 0;
        int cellY = 0;
        final int cellWidth = pixelsPerCellX;

        for (int x = 0; x < grid.size(); x++)
        {
            final LinkedList<GridCell> cells = grid.get(x);
            for (int y = 0; y < cells.size(); y++)
            {
                final GridCell cell = cells.get(y);
                g.setColor(getColor(cell.getTemp()));
                g.fillRect(cellX, cellY, cell.getWidth(), cell.getHeight());
                cellY += cell.getHeight();
            }
            cellX += cellWidth;
            cellY = 0;
        }
    }

    private void drawEarthImage(final Graphics g)
    {
        g.drawImage(image.getImage(), 0, 0, null);
    }

    private void drawGrid(final Graphics g)
    {
        g.setColor(Color.black);

        // draw longitude lines
        for (int x = 0; x <= imgWidth; x += pixelsPerCellX)
        {
            g.drawLine(x, 0, x, imgHeight);
        }

        // draw scaled latitude lines
        for (int lat = 0; lat <= 90; lat += settings.getGridSpacing())
        {
            final int y = (int) calculateDistanceToEquator(lat, radius);
            g.drawLine(0, radius - y, imgWidth, radius - y);
            g.drawLine(0, radius + y, imgWidth, radius + y);
        }

        g.setColor(Color.blue);
        g.drawLine(imgWidth / 2, 0, imgWidth / 2, imgHeight); // prime meridian
        g.drawLine(0, imgHeight / 2, imgWidth, imgHeight / 2); // equator
    }

    public void reset()
    {
        init();
    }

    public int getRadius()
    {
        return radius;
    }

    public float calculateAverageTemperature()
    {
        final LinkedList<LinkedList<GridCell>> grid = gridSettings.getGrid();

        float totalTemp = 0;

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

    private Color getColor(final double tempInCelcius)
    {
        final Color c = Color.getHSBColor((float) (.666 * tempInCelcius), 1f, 1f);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 120);
    }
}
