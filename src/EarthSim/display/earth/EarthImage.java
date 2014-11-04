package EarthSim.display.earth;

import java.net.URL;

import javax.swing.ImageIcon;

public class EarthImage extends ImageIcon
{
    private static final long serialVersionUID = 1L;
    private final static URL  URL              = EarthImage.class.getResource("EarthImage.png");

    public EarthImage()
    {
        super(URL.getPath());
    }
}