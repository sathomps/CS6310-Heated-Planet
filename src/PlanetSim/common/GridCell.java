package PlanetSim.common;

public class GridCell
{
    private static final int DEFAULT_CELL_TEMPERATURE_CELCIUS = 85;

    private int              width;
    private int              height;

    private float            latitudeTop;
    private float            latitudeBottom;
    private float            longitudeLeft;
    private float            longitudeRight;

    private float            surfaceArea;

    private float            temp                             = DEFAULT_CELL_TEMPERATURE_CELCIUS;
    private float            oldTemp                          = DEFAULT_CELL_TEMPERATURE_CELCIUS;

    private GridCell         north;
    private GridCell         south;
    private GridCell         west;
    private GridCell         east;

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

    public float getLatitudeTop()
    {
        return latitudeTop;
    }

    public void setLatitudeTop(final float latitudeTop)
    {
        this.latitudeTop = latitudeTop;
    }

    public float getLatitudeBottom()
    {
        return latitudeBottom;
    }

    public void setLatitudeBottom(final float latitudeBottom)
    {
        this.latitudeBottom = latitudeBottom;
    }

    public float getLongitudeLeft()
    {
        return longitudeLeft;
    }

    public void setLongitudeLeft(final float longitudeLeft)
    {
        this.longitudeLeft = longitudeLeft;
    }

    public float getLongitudeRight()
    {
        return longitudeRight;
    }

    public void setLongitudeRight(final float longitudeRight)
    {
        this.longitudeRight = longitudeRight;
    }

    public float getSurfaceArea()
    {
        return surfaceArea;
    }

    public void setSurfaceArea(final float surfaceArea)
    {
        this.surfaceArea = surfaceArea;
    }

    public float getTemp()
    {
        return temp;
    }

    public void setTemp(final float temp)
    {
        this.temp = temp;
    }

    public float getWestTemp()
    {
        return west == null ? 0.0f : west.getTemp();
    }

    public float getEastTemp()
    {
        return east == null ? 0.0f : east.getTemp();
    }

    public float getSouthTemp()
    {
        return south == null ? 0.0f : south.getTemp();
    }

    public float getNorthTemp()
    {
        return north == null ? 0.0f : north.getTemp();
    }

    public void swapTemp()
    {
        this.oldTemp = this.temp;
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
}