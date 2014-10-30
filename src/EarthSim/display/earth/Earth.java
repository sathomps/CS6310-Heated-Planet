package EarthSim.display.earth;

import static EarthSim.display.earth.EarthImage.IMAGE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.LinkedList;

import javax.swing.JPanel;

import EarthSim.common.GridCell;
import EarthSim.common.GridSettings;
import EarthSim.common.SimulationSettings;
import EarthSim.common.Util;

public class Earth extends JPanel
{
    private static final long        serialVersionUID = 1L;

    private static final float       OPACITY          = .78f;

    private BufferedImage            imgTransparent;
    private final float[]            scales           = { 1f, 1f, 1f, OPACITY };
    private final float[]            offsets          = new float[4];
    private int                      gridSpacing;
    private int                      pixelsPerCellX;
    private int                      pixelsPerCellY;
    private int                      imgWidth;
    private int                      imgHeight;
    private int                      numCellsX;
    private int                      numCellsY;
    private int                      radius;
    private final SimulationSettings settings;
    private GridSettings             gridSettings;

    /**
     * Constructs a display grid with a default grid spacing.
     * 
     * @param settings
     */
    public Earth(final SimulationSettings settings)
    {
        this.settings = settings;
        settings.setEarth(this);
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
        this.gridSpacing = settings.getGridSpacing();

        numCellsX = 360 / gridSpacing;
        pixelsPerCellX = IMAGE.getWidth() / numCellsX;
        imgWidth = numCellsX * pixelsPerCellX;

        numCellsY = 180 / gridSpacing;
        pixelsPerCellY = IMAGE.getHeight() / numCellsY;
        imgHeight = numCellsY * pixelsPerCellY;
        radius = imgHeight / 2;

        // create an image capable of transparency; then draw our image into it
        imgTransparent = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics g = imgTransparent.getGraphics();

        gridSettings.setHeight(imgHeight).setWidth(imgWidth);

        g.drawImage(IMAGE, 0, 0, imgWidth, imgHeight, null);
    }

    @Override
    public void paint(final Graphics g)
    {
        fillCellColors(g);
        drawTransparentImage(g);
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
                g.setColor(cell.getTempColor());

                g.fillRect(cellX, cellY, cell.getWidth(), (int) cell.getHeight());
                cellY += cell.getHeight();
            }
            cellX += cellWidth;
            cellY = 0;
        }
    }

    private void drawTransparentImage(final Graphics g)
    {
        final RescaleOp rop = new RescaleOp(scales, offsets, null);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(imgTransparent, rop, 0, 0);
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
        for (int lat = 0; lat <= 90; lat += gridSpacing)
        {
            final int y = (int) Util.calculateDistanceToEquator(lat, radius);
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
}
