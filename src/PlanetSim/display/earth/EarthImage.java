package PlanetSim.display.earth;

import java.net.URL;

import javax.swing.ImageIcon;

public class EarthImage extends ImageIcon
{
    private static final long serialVersionUID = 1L;
    private final static URL  URL              = EarthImage.class.getResource("earth-800x400.jpg");

    public EarthImage()
    {
        super(URL.getPath());
    }
}