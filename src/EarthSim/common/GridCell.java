package EarthSim.common;

import static EarthSim.common.TemperatureColorPicker.getColor;

import java.awt.Color;

public class GridCell
{
    private static final int         DEFAULT_CELL_TEMPERATURE_CELCIUS = 85;

    private int                      width;
    private double                   height;
    private double                   surfaceArea;

    private int                      temp                             = DEFAULT_CELL_TEMPERATURE_CELCIUS;
    private int                      oldTemp                          = DEFAULT_CELL_TEMPERATURE_CELCIUS;

    private GridCell                 top;
    private GridCell                 bottom;
    private GridCell                 left;
    private GridCell                 right;

    private float                    topLength;
    private float                    bottomLength;
    private float                    sideLength;

    private float                    latitudeTop;
    private float                    latitudeBottom;
    private float                    longitudeLeft;
    private float                    longitudeRight;
    private final SimulationSettings settings;

    public GridCell(final SimulationSettings settings)
    {
        this.settings = settings;
    }

    public GridCell getTop()
    {
        return top;
    }

    public void setTop(final GridCell top)
    {
        this.top = top;
    }

    public GridCell getBottom()
    {
        return bottom;
    }

    public void setBottom(final GridCell bottom)
    {
        this.bottom = bottom;
    }

    public GridCell getLeft()
    {
        return left;
    }

    public void setLeft(final GridCell left)
    {
        this.left = left;
    }

    public GridCell getRight()
    {
        return right;
    }

    public void setRight(final GridCell right)
    {
        this.right = right;
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

    public int getWidth()
    {
        return width;
    }

    public void setWidth(final int width)
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(final double height)
    {
        this.height = height;
    }

    public double getSurfaceArea()
    {
        return surfaceArea;
    }

    public void setSurfaceArea(final double surfaceArea)
    {
        this.surfaceArea = surfaceArea;
    }

    public int getTemp()
    {
        return temp;
    }

    public void setTemp(final int temp)
    {
        this.temp = temp;
    }

    public Color getTempColor()
    {
        return getColor(temp);
    }

    private void calculateLengths()
    {
        final int earthRadius = settings.getEarthRadius();
        final float sepDeg = latitudeTop - latitudeBottom;
        this.topLength = Util.calculateLatitudeCircum(latitudeTop, earthRadius) / (360 / Math.abs(sepDeg));
        this.bottomLength = Util.calculateLatitudeCircum(latitudeBottom, earthRadius) / (360 / Math.abs(sepDeg));
        this.sideLength = Util.calculateTrapezoidSideLen(topLength, bottomLength, height);
    }

    private void calculateArea()
    {
        this.surfaceArea = Util.calculateTrapezoidArea(topLength, bottomLength, height);
    }

    private void calculateHeight()
    {
        final int earthRadius = settings.getEarthRadius();

        this.height = Math.abs(Util.calculateDistanceToEquator(latitudeTop, earthRadius) - Util.calculateDistanceToEquator(latitudeBottom, earthRadius));
    }

    public void calculateGeometry()
    {
        this.calculateHeight();
        this.calculateLengths();
        this.calculateArea();
    }

    private float calculateNeighborHeat()
    {
        final float totalLen = topLength + bottomLength + sideLength + sideLength;

        final float l = (left != null) ? left.oldTemp : 0;
        final float r = (right != null) ? right.oldTemp : 0;
        final float t = (top != null) ? top.oldTemp : 0;
        final float b = (bottom != null) ? bottom.oldTemp : 0;

        final float result = ((sideLength / totalLen) * l) + ((sideLength / totalLen) * r) + ((topLength / totalLen) * t) + ((bottomLength / totalLen) * b);

        return result;
    }

    public void swapTemp()
    {
        this.oldTemp = this.temp;
    }

    public void calculateTemp()
    {
        temp = Math.round(settings.calculateSunHeat(this) + calculateNeighborHeat());
    }
}