package PlanetSim.common;

import static PlanetSim.common.util.GeoUtil.calculateDistanceToEquator;
import static PlanetSim.common.util.GeoUtil.calculateLatitudeCircum;
import static PlanetSim.common.util.GeoUtil.calculateTrapezoidArea;

import java.util.LinkedList;

import PlanetSim.model.GridCell;

public class GridSettings
{
    private static final double                    LATITUDE_TOP    = 90;
    private static final double                    LATITUDE_BOTTOM = -90;

    private static final double                    LONGITUDE_LEFT  = 180;
    private static final double                    LONGITUDE_RIGHT = -180;

    private final LinkedList<LinkedList<GridCell>> grid            = new LinkedList<LinkedList<GridCell>>();

    private final SimulationSettings               settings;

    public GridSettings(final SimulationSettings settings)
    {
        this.settings = settings;
    }

    public LinkedList<LinkedList<GridCell>> getGrid()
    {
        return grid;
    }

    public void addCell(final int row, final int col)
    {
        final GridCell cell = new GridCell();
        cell.setWidth(settings.getPixelsPerCellX());
        LinkedList<GridCell> cols;
        try
        {
            cols = grid.get(row);
        }
        catch (final IndexOutOfBoundsException ex)
        {
            cols = new LinkedList<GridCell>();
            grid.add(cols);
        }
        cols.add(cell);
        calculateCoordinates(row, col, cell);
        calculateGeometry(cell);
    }

    public GridCell getCellByRowCol(final int row, final int col)
    {
        return grid.get(row).get(col);
    }

    private void calculateCoordinates(final int row, final int col, final GridCell cell)
    {
        cell.setRow(row);
        cell.setColumn(col);

        final int spacing = settings.getGridSpacing();

        if (col == 0)
        {
            cell.setLatitudeTop(LATITUDE_TOP);
            cell.setLatitudeBottom(((LATITUDE_TOP / spacing) % 1) == 0 ? (cell.getLatitudeTop() - spacing)
                    : (cell.getLatitudeTop() - (spacing * ((LATITUDE_TOP / spacing) % 1))));
        }
        else if ((col > 0) && (col < (calculateGridHeight() - 1)))
        {
            cell.setLatitudeTop(getCellByRowCol(row, col - 1).getLatitudeBottom());
            cell.setLatitudeBottom(cell.getLatitudeTop() - spacing);

            cell.setTop(getCellByRowCol(row, col - 1));
            getCellByRowCol(row, col - 1).setBottom(cell);
        }
        else if (col == (calculateGridHeight() - 1))
        {
            cell.setLatitudeTop(getCellByRowCol(row, col - 1).getLatitudeBottom());
            cell.setLatitudeBottom(LATITUDE_BOTTOM);

            cell.setTop(getCellByRowCol(row, col - 1));
            getCellByRowCol(row, col - 1).setBottom(getCellByRowCol(row, col));
        }

        if (row == 0)
        {
            cell.setLongitudeLeft(LONGITUDE_LEFT);
            cell.setLongitudeRight((((LONGITUDE_LEFT / spacing) % 1) == 0) ? (cell.getLongitudeLeft() - spacing)
                    : (cell.getLongitudeLeft() - (spacing * ((LONGITUDE_LEFT / spacing) % 1))));
        }
        else if ((row > 0) && (row < (settings.getPixelsPerCellX() - 1)))
        {
            cell.setLongitudeLeft(getCellByRowCol(row - 1, col).getLongitudeRight());
            cell.setLongitudeRight(cell.getLongitudeLeft() - spacing);

            cell.setLeft(getCellByRowCol(row - 1, col));
            getCellByRowCol(row - 1, col).setRight(cell);
        }
        else if (row == (settings.getPixelsPerCellX() - 1))
        {
            cell.setLongitudeLeft(getCellByRowCol(row - 1, col).getLongitudeRight());
            cell.setLongitudeRight(LONGITUDE_RIGHT);

            cell.setLeft(getCellByRowCol(row - 1, col));
            getCellByRowCol(row - 1, col).setRight(getCellByRowCol(row, col));

            cell.setRight(getCellByRowCol(0, col));

            getCellByRowCol(0, col).setLeft(getCellByRowCol(row, col));
        }
    }

    private void calculateLengths(final GridCell cell)
    {
        final int planetRadius = settings.getPlanetRadius();
        final double sepDeg = cell.getLatitudeTop() - cell.getLatitudeBottom();
        final double topLength = calculateLatitudeCircum(cell.getLatitudeTop(), planetRadius) / (360 / Math.abs(sepDeg));
        final double bottomLength = calculateLatitudeCircum(cell.getLatitudeBottom(), planetRadius) / (360 / Math.abs(sepDeg));
        calculateSurfaceArea(cell, topLength, bottomLength);
    }

    private void calculateSurfaceArea(final GridCell cell, final double topLength, final double bottomLength)
    {
        cell.setSurfaceArea(calculateTrapezoidArea(topLength, bottomLength, cell.getHeight()));
    }

    private void calculateHeight(final GridCell cell)
    {
        final int earthRadius = settings.getPlanetRadius();

        cell.setHeight((int) Math.abs(calculateDistanceToEquator(cell.getLatitudeTop(), earthRadius)
                - calculateDistanceToEquator(cell.getLatitudeBottom(), earthRadius)));
    }

    private int calculateGridHeight()
    {
        return settings.getPlanetRadius() * 2;
    }

    private void calculateGeometry(final GridCell cell)
    {
        calculateHeight(cell);
        calculateLengths(cell);
    }

    public void addCell(final int row, final int col, final int gridSpacing, final double temp, final double longLeft, final double latTop,
            final double longRight, final double latBottom, final long read_dt, final int read_tm)
    {
        final GridCell cell = new GridCell();
        cell.setWidth(settings.getPixelsPerCellX());
        cell.setTemp((int) temp);
        cell.setLatitudeBottom(latBottom);
        cell.setLatitudeTop(latTop);
        cell.setLongitudeLeft(longLeft);
        cell.setLongitudeRight(longRight);
        cell.setRow(row);
        cell.setColumn(col);
        LinkedList<GridCell> cols;

        try
        {
            cols = grid.get(row);
        }
        catch (final IndexOutOfBoundsException ex)
        {
            cols = new LinkedList<GridCell>();
            grid.add(cols);
        }
        cols.add(cell);
        calculateGeometry(cell);
    }

    public int size()
    {
        return grid.size() * grid.get(0).size();
    }
}
