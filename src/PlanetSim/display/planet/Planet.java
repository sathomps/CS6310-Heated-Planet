package PlanetSim.display.planet;

import static PlanetSim.common.util.GeoUtil.calculateDistanceToEquator;
import static PlanetSim.common.util.PlanetTemperatureUtil.getColor;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.StopEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.model.GridCell;

public class Planet extends JPanel
{
    private static final long  serialVersionUID = 1L;

    private static final int   WIDTH            = 800;
    private static final int   HEIGHT           = 400;

    private int                pixelsPerCellX;
    private int                pixelsPerCellY;
    private int                imgWidth;
    private int                imgHeight;
    private int                numCellsX;
    private int                numCellsY;
    private int                radius;
    private SimulationSettings settings;
    private GridSettings       gridSettings;

    /**
     * Constructs a display grid with a default grid spacing.
     * 
     * @param settings
     */
    public Planet(final SimulationSettings settings)
    {
        this.settings = settings;
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
        pixelsPerCellX = WIDTH / numCellsX;
        imgWidth = numCellsX * pixelsPerCellX;

        numCellsY = 180 / settings.getGridSpacing();
        pixelsPerCellY = HEIGHT / numCellsY;
        imgHeight = numCellsY * pixelsPerCellY;
        radius = imgHeight / 2;

        gridSettings.setHeight(imgHeight).setWidth(imgWidth);
    }

    @Override
    public void paint(final Graphics g)
    {
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

    private void drawGrid(final Graphics g)
    {
        g.setColor(Color.black);

        drawLongitudeLines(g);

        drawLatitudeLines(g);

        drawPrimeMeridianEquator(g);
    }

    private void drawPrimeMeridianEquator(final Graphics g)
    {
        g.setColor(Color.blue);
        g.drawLine(imgWidth / 2, 0, imgWidth / 2, imgHeight); // prime meridian
        g.drawLine(0, imgHeight / 2, imgWidth, imgHeight / 2); // equator
    }

    private void drawLatitudeLines(final Graphics g)
    {
        for (int lat = 0; lat <= 90; lat += settings.getGridSpacing())
        {
            final int y = (int) calculateDistanceToEquator(lat, radius);
            g.drawLine(0, radius - y, imgWidth, radius - y);
            g.drawLine(0, radius + y, imgWidth, radius + y);
        }
    }

    private void drawLongitudeLines(final Graphics g)
    {
        for (int x = 0; x <= imgWidth; x += pixelsPerCellX)
        {
            g.drawLine(x, 0, x, imgHeight);
        }
    }

    @Subscribe
    public void reset(final StopEvent event)
    {
        init();
    }

    @Subscribe
    public void process(final DisplayEvent displayEvent)
    {
        settings = displayEvent.getSettings();
        repaint();
    }

    public int getRadius()
    {
        return radius;
    }
}
