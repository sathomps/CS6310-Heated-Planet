package PlanetSim.display.planet;

import static PlanetSim.common.util.GeoUtil.calculateDistanceToEquator;
import static PlanetSim.common.util.PlanetTemperatureUtil.getColor;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

import PlanetSim.common.GridSettings;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.StopEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.model.GridCell;

public class Planet extends JPanel
{
    private static final long  serialVersionUID = 1L;

    private SimulationSettings settings;
    private GridSettings       gridSettings;

    /**
     * Constructs a display grid with a default grid spacing.
     * 
     * @param settings
     */
    public Planet(final EventBus eventBus, final SimulationSettings settings)
    {
        this.settings = settings;
        eventBus.subscribe(this);
    }

    @Override
    public void paint(final Graphics g)
    {
        fillCellColors(g);
        drawGrid(g);
    }

    private void fillCellColors(final Graphics g)
    {
        final LinkedList<LinkedList<GridCell>> grid = gridSettings.getGrid();

        int cellX = 0;
        int cellY = 0;
        final int cellWidth = settings.getPixelsPerCellX();

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
        // prime meridian
        g.drawLine(settings.getPlanetWidth() / 2, 0, settings.getPlanetWidth() / 2, settings.getPlanetHeight());
        // equator
        g.drawLine(0, settings.getPlanetHeight() / 2, settings.getPlanetWidth(), settings.getPlanetHeight() / 2);
    }

    private void drawLatitudeLines(final Graphics g)
    {
        for (int lat = 0; lat <= 90; lat += settings.getGridSpacing())
        {
            final int y = (int) calculateDistanceToEquator(lat, settings.getPlanetRadius());
            g.drawLine(0, settings.getPlanetRadius() - y, settings.getPlanetWidth(), settings.getPlanetRadius() - y);
            g.drawLine(0, settings.getPlanetRadius() + y, settings.getPlanetWidth(), settings.getPlanetRadius() + y);
        }
    }

    private void drawLongitudeLines(final Graphics g)
    {
        for (int x = 0; x <= settings.getPlanetWidth(); x += settings.getPixelsPerCellX())
        {
            g.drawLine(x, 0, x, settings.getPlanetHeight());
        }
    }

    @Subscribe
    public void reset(final StopEvent event)
    {
    }

    @Subscribe
    public void process(final DisplayEvent displayEvent)
    {
        settings = displayEvent.getSettings();
        repaint();
    }
}
