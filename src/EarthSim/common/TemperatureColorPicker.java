package EarthSim.common;

import java.awt.Color;

public final class TemperatureColorPicker
{
    private TemperatureColorPicker()
    {
    }

    public static Color getColor(int tempInCelcius)
    {
        int b = 0;
        int g = 0;
        int r = 0;

        if (tempInCelcius <= -100)
        {
            b = 170;
            g = 100;
            r = 170;
        }
        else if (tempInCelcius <= -46)
        {
            tempInCelcius = -1 * tempInCelcius;
            b = 255;
            g = 145 - ((tempInCelcius * 10) % 115);
            r = 255;
        }
        else if ((tempInCelcius <= -23) && (tempInCelcius > -46))
        {
            tempInCelcius = -1 * tempInCelcius;
            b = 255;
            g = 145;
            r = 145 + ((tempInCelcius * 5) % 115);
        }
        else if ((tempInCelcius < 0) && (tempInCelcius > -23))
        {
            tempInCelcius = -1 * tempInCelcius;
            b = 255;
            g = 145;
            r = 145 - (tempInCelcius * 5);
        }
        else if (tempInCelcius == 0)
        {
            b = 225;
            g = 145;
            r = 145;
        }
        else if ((tempInCelcius > 0) && (tempInCelcius < 23))
        {
            b = 255;
            g = 145 + (tempInCelcius * 5);
            r = 145;
        }
        else if ((tempInCelcius >= 23) && (tempInCelcius < 46))
        {
            b = 255 - ((tempInCelcius * 5) % 115);
            g = 255;
            r = 145;
        }
        else if ((tempInCelcius >= 46) && (tempInCelcius < 69))
        {
            b = 145;
            g = 255;
            r = 145 + ((tempInCelcius * 5) % 115);
        }
        else if ((tempInCelcius >= 69) && (tempInCelcius < 92))
        {
            b = 145;
            g = 255 - ((tempInCelcius * 5) % 115);
            r = 255;
        }
        else
        {
            b = 145 - ((tempInCelcius * 10) % 115);
            g = 145 - ((tempInCelcius * 10) % 115);
            r = 255;
        }

        return new Color(r, g, b);
    }
}
