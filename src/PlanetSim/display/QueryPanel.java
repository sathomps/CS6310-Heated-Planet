package PlanetSim.display;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;

public class QueryPanel extends JPanel
{
    private static final long         serialVersionUID = 1L;

    private static final Dimension    PREFERRED_SIZE   = new Dimension(150, 100);

    private final SimulationSettings  settings;

    private final EventBus            eventBus;

    public QueryPanel(final EventBus eventBus, final SimulationSettings settings)
    {
        this.eventBus = eventBus;
        this.settings = settings;
        initLayout();
        
        addQuerySettings();
    }

    private void initLayout()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setMinimumSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setPreferredSize(PREFERRED_SIZE);
    }

	private void addQuerySettings()
    {
    }
}
