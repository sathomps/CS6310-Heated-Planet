package PlanetSim.model;

public class GridCell
{
    private static final int DEFAULT_CELL_TEMPERATURE_CELCIUS = 85;

    private int              width;
    private int              height;

    private double           latitudeTop;
    private double           latitudeBottom;
    private double           longitudeLeft;
    private double           longitudeRight;

    private double           surfaceArea;

    private double           temp                             = DEFAULT_CELL_TEMPERATURE_CELCIUS;

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

    private int readDate = 0;
    private int readTime = 0;

    public int getDate()
    {
        return readDate;
    }

    public void setDate(final int readDate)
    {
        this.readDate = readDate;
    }

    public int getTime()
    {
        return readTime;
    }

    public void setTime(final int readTime)
    {
        this.readTime = readTime;
    }

    @Override
    public String toString()
    {
        return "GridCell [width=" + width + ", height=" + height + ", latitudeTop=" + latitudeTop + ", latitudeBottom=" + latitudeBottom + ", longitudeLeft="
                + longitudeLeft + ", longitudeRight=" + longitudeRight + ", surfaceArea=" + surfaceArea + ", temp=" + temp + ", north=" + north + ", south="
                + south + ", west=" + west + ", east=" + east + ", readDate=" + readDate + ", readTime=" + readTime + "]";
    }
}