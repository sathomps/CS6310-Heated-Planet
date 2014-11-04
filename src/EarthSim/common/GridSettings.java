package EarthSim.common;

import java.util.LinkedList;

public class GridSettings
{
    private static final float                     LATITUDE_TOP    = 90;
    private static final float                     LATITUDE_BOTTOM = -90;

    private static final float                     LONGITUDE_LEFT  = 180;
    private static final float                     LONGITUDE_RIGHT = -180;

    private int                                    width;
    private int                                    height;
    private final LinkedList<LinkedList<GridCell>> grid            = new LinkedList<LinkedList<GridCell>>();

    private final SimulationSettings               settings;

    public GridSettings(final SimulationSettings settings)
    {
        this.settings = settings;
        settings.setGridSettings(this);
    }

    public LinkedList<LinkedList<GridCell>> getGrid()
    {
        return grid;
    }

    public GridSettings setWidth(final int width)
    {
        this.width = width;
        return this;
    }

    public int getWidth()
    {
        return width;
    }

    public GridSettings setHeight(final int height)
    {
        this.height = height;
        return this;
    }

    public int getHeight()
    {
        return height;
    }

    public void addCell(final int row, final int col, final int width)
    {
        final GridCell cell = new GridCell();
        cell.setWidth(width);

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
        final int spacing = settings.getGridSpacing();

        if (col == 0)
        {
            cell.setLatitudeTop(LATITUDE_TOP);
            cell.setLatitudeBottom(((LATITUDE_TOP / spacing) % 1) == 0 ? (cell.getLatitudeTop() - spacing)
                    : (cell.getLatitudeTop() - (spacing * ((LATITUDE_TOP / spacing) % 1))));
        }
        else if ((col > 0) && (col < (height - 1)))
        {
            cell.setLatitudeTop(getCellByRowCol(row, col - 1).getLatitudeBottom());
            cell.setLatitudeBottom(cell.getLatitudeTop() - spacing);

            cell.setTop(getCellByRowCol(row, col - 1));
            getCellByRowCol(row, col - 1).setBottom(cell);
        }
        else if (col == (height - 1))
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
        else if ((row > 0) && (row < (width - 1)))
        {
            cell.setLongitudeLeft(getCellByRowCol(row - 1, col).getLongitudeRight());
            cell.setLongitudeRight(cell.getLongitudeLeft() - spacing);

            cell.setLeft(getCellByRowCol(row - 1, col));
            getCellByRowCol(row - 1, col).setRight(cell);
        }
        else if (row == (width - 1))
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
        final int earthRadius = settings.getEarthRadius();
        final float sepDeg = cell.getLatitudeTop() - cell.getLatitudeBottom();
        final float topLength = Util.calculateLatitudeCircum(cell.getLatitudeTop(), earthRadius) / (360 / Math.abs(sepDeg));
        final float bottomLength = Util.calculateLatitudeCircum(cell.getLatitudeBottom(), earthRadius) / (360 / Math.abs(sepDeg));
        // final float sideLength = Util.calculateTrapezoidSideLen(topLength,
        // bottomLength, height);
        calculateSurfaceArea(cell, topLength, bottomLength);
    }

    private void calculateSurfaceArea(final GridCell cell, final float topLength, final float bottomLength)
    {
        cell.setSurfaceArea(Util.calculateTrapezoidArea(topLength, bottomLength, cell.getHeight()));
    }

    private void calculateHeight(final GridCell cell)
    {
        final int earthRadius = settings.getEarthRadius();

        cell.setHeight((int) Math.abs(Util.calculateDistanceToEquator(cell.getLatitudeTop(), earthRadius)
                - Util.calculateDistanceToEquator(cell.getLatitudeBottom(), earthRadius)));
    }

    private void calculateGeometry(final GridCell cell)
    {
        calculateHeight(cell);
        calculateLengths(cell);
    }

}
