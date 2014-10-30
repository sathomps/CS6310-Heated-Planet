package EarthSim.display;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import EarthSim.common.SimulationSettings;
import EarthSim.display.earth.Earth;
import EarthSim.display.sun.Sun;

/**
 * A {@link JPanel} composed of the the earth and sun display components.
 * 
 * @author Andrew Bernard
 */
public class EarthSunPanel extends JPanel
{
    private static final long        serialVersionUID = 1l;

    private static final Dimension   PREFERRED_SIZE   = new Dimension(800, 400);

    private Sun                      sun;
    private Earth                    earth;

    private final SimulationSettings settings;

    @SuppressWarnings("unused")
    private RepaintTask              repaintTask;

    public EarthSunPanel(final SimulationSettings settings)
    {
        this.settings = settings;

        initLayout();
        initEarth();
        initSun();

        addSunAndEarth();
        drawGrid();

        initRepaintTask();
    }

    private void initRepaintTask()
    {
        repaintTask = new RepaintTask();
    }

    private void addSunAndEarth()
    {
        add(sun);
        add(earth);
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

    private void initEarth()
    {
        earth = new Earth(settings);
        earth.setAlignmentX(Component.LEFT_ALIGNMENT);
        earth.init();
    }

    private void initSun()
    {
        sun = new Sun(settings);
        sun.setAlignmentX(Component.LEFT_ALIGNMENT);
        sun.init();
    }

    private void drawGrid()
    {
        sun.drawSunPath();
        repaint();
    }

    private class RepaintTask extends Timer
    {
        private RepaintTask()
        {
            scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    repaint();
                }
            }, 0, MILLISECONDS.convert(250, MILLISECONDS));
        }
    }

}
