package PlanetSim.model;

public class GridCell
{
    private static final double DEFAULT_CELL_TEMPERATURE_CELCIUS = 14.85;
    private static final int    MAX_CELL_TEMPERATURE_CELCIUS     = 58;

    private int                 row;
    private int                 column;

    private int                 width;
    private int                 height;

    private double              latitudeTop;
    private double              latitudeBottom;
    private double              longitudeLeft;
    private double              longitudeRight;

    private double              surfaceArea;

    private double              temp                             = DEFAULT_CELL_TEMPERATURE_CELCIUS;

    private GridCell            north;
    private GridCell            south;
    private GridCell            west;
    private GridCell            east;

    public GridCell getNorth()
    {
        return north;
    }

    public void setNorth(final GridCell north)
    {
        this.north = north;
    }

    public GridCell getSouth()
    {
        return south;
    }

    public void setSouth(final GridCell south)
    {
        this.south = south;
    }

    public GridCell getWest()
    {
        return west;
    }

    public void setWest(final GridCell west)
    {
        this.west = west;
    }

    public GridCell getEast()
    {
        return east;
    }

    public void setEast(final GridCell east)
    {
        this.east = east;
    }

    public GridCell getTop()
    {
        return north;
    }

    public void setTop(final GridCell top)
    {
        this.north = top;
    }

    public GridCell getBottom()
    {
        return south;
    }

    public void setBottom(final GridCell bottom)
    {
        this.south = bottom;
    }

    public GridCell getLeft()
    {
        return west;
    }

    public void setLeft(final GridCell left)
    {
        this.west = left;
    }

    public GridCell getRight()
    {
        return east;
    }

    public void setRight(final GridCell right)
    {
        this.east = right;
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

    public double getSurfaceArea()
    {
        return surfaceArea;
    }

    public void setSurfaceArea(final double surfaceArea)
    {
        this.surfaceArea = surfaceArea;
    }

    public double getTemp()
    {
        return temp;
    }

    public void setTemp(final double temp)
    {
        this.temp = temp;
        validateTemp();
    }

    private void validateTemp()
    {
        if (temp > MAX_CELL_TEMPERATURE_CELCIUS)
        {
            temp = MAX_CELL_TEMPERATURE_CELCIUS;
        }
    }

    public double getWestTemp()
    {
        return west == null ? 0.0 : west.getTemp();
    }

    public double getEastTemp()
    {
        return east == null ? 0.0 : east.getTemp();
    }

    public double getSouthTemp()
    {
        return south == null ? 0.0 : south.getTemp();
    }

    public double getNorthTemp()
    {
        return north == null ? 0.0 : north.getTemp();
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(final int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(final int width)
    {
        this.width = width;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("GridCell [getNorth()=");
        // builder.append(getNorth());
        builder.append(", getSouth()=");
        // builder.append(getSouth());
        builder.append(", getWest()=");
        // builder.append(getWest());
        builder.append(", getEast()=");
        // builder.append(getEast());
        builder.append(", getTop()=");
        // builder.append(getTop());
        builder.append(", getBottom()=");
        // builder.append(getBottom());
        builder.append(", getLeft()=");
        // builder.append(getLeft());
        builder.append(", getRight()=");
        // builder.append(getRight());
        builder.append(", getLatitudeTop()=");
        builder.append(getLatitudeTop());
        builder.append(", getLatitudeBottom()=");
        builder.append(getLatitudeBottom());
        builder.append(", getLongitudeLeft()=");
        builder.append(getLongitudeLeft());
        builder.append(", getLongitudeRight()=");
        builder.append(getLongitudeRight());
        builder.append(", getSurfaceArea()=");
        builder.append(getSurfaceArea());
        builder.append(", getTemp()=");
        builder.append(getTemp());
        builder.append(", getWestTemp()=");
        builder.append(getWestTemp());
        builder.append(", getEastTemp()=");
        builder.append(getEastTemp());
        builder.append(", getSouthTemp()=");
        builder.append(getSouthTemp());
        builder.append(", getNorthTemp()=");
        builder.append(getNorthTemp());
        builder.append(", getHeight()=");
        builder.append(getHeight());
        builder.append(", getWidth()=");
        builder.append(getWidth());
        builder.append("]");
        return builder.toString();
    }

    public int getRow()
    {
        return row;
    }

    public void setRow(final int row)
    {
        this.row = row;
    }

    public int getColumn()
    {
        return column;
    }

    public void setColumn(final int column)
    {
        this.column = column;
    }
}