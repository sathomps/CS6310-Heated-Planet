package EarthSim.display.earth;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class EarthImage
{
    private final static URL    URL = EarthImage.class.getResource("EarthImage.jpg");
    public static BufferedImage IMAGE;

    static
    {
        try
        {
            IMAGE = ImageIO.read(URL);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}