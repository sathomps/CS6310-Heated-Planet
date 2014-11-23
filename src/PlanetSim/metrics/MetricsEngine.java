package PlanetSim.metrics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.db.DBEngine;
import PlanetSim.simulation.SimulationEngineDaemon;

public class MetricsEngine
{
    private static final SimpleDateFormat      SDF      = new SimpleDateFormat("hh:mm:ss.SSS");
    private static final String                LINE_SEP = System.getProperty("line.separator");

    private final EventBus                     eventBus;

    private Writer                             writer;

    private static final OperatingSystemMXBean OS_BEAN  = ManagementFactory.getOperatingSystemMXBean();

    public MetricsEngine(final EventBus eventBus)
    {
        this.eventBus = eventBus;
        this.eventBus.subscribe(this);
        try
        {
            writer = new OutputStreamWriter(new FileOutputStream("metrics_" + new Date().getTime() + ".csv"), "utf-8");
            outputMetricsHeader();
        }
        catch (final Exception ex)
        {
        }
    }

    private void outputMetricsHeader() throws IOException
    {
        final StringBuilder line = new StringBuilder();
        line.append("SimulationName");
        line.append(",");
        line.append("DataSource");
        line.append(",");
        line.append("DataStoragePrecision");
        line.append(",");
        line.append("GeographicPrecision");
        line.append(",");
        line.append("TemporalPrecision");
        line.append(",");
        line.append("GridSpacing");
        line.append(",");
        line.append("QueryTime");
        line.append(",");
        line.append("DatabaseSize");
        line.append(",");
        line.append("UsedSystemMemory");
        line.append(",");
        line.append("CPU %");
        line.append(",");
        line.append("Date");
        writer.append(line);
        writer.append(LINE_SEP);
        writer.flush();
    }

    @Subscribe
    public void subscribe(final MetricEvent event)
    {
        try
        {
            final SimulationSettings settings = event.getSettings();
            final StringBuilder line = new StringBuilder();
            line.append(settings.getSimulationName());
            line.append(",");
            line.append(settings.getDataSource());
            line.append(",");
            line.append(settings.getDatastoragePrecision());
            line.append(",");
            line.append(settings.getGeographicPrecision());
            line.append(",");
            line.append(settings.getTemporalPrecision());
            line.append(",");
            line.append(settings.getGridSpacing());
            line.append(",");
            line.append(event.getQueryTime());
            line.append(",");
            line.append(event.getDatabaseSize());
            line.append(",");
            line.append(getMemUsed());
            line.append(",");
            line.append(getCpuLoad());
            line.append(",");
            line.append(SDF.format(new Date()));
            writer.append(line);
            writer.append(LINE_SEP);
            writer.flush();
        }
        catch (final Exception ex)
        {
        }
    }

    private double getMemUsed()
    {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
    }

    private static double getCpuLoad()
    {
        try
        {
            final double value = OS_BEAN.getSystemLoadAverage();

            if (value != -1.0)
            {
                return ((int) (value * 1000) / 10.0);
            }
        }
        catch (final Exception e)
        {
        }
        return 0.0;
    }

    public static void main(final String[] args)
    {
        final SimulationSettings settings = new SimulationSettings();
        setCommandLineArgs(args, settings);

        settings.setSimulationName("test_" + new Date().getTime());
        final EventBus eventBus = EventBus.getInstance();
        new DBEngine(eventBus);
        new SimulationEngineDaemon(eventBus);
        new MetricsEngine(eventBus);

        final RunEvent event = new RunEvent(settings);
        eventBus.publish(event);
    }

    private static void setCommandLineArgs(final String[] args, final SimulationSettings settings)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-t"))
            {
                settings.setTemporalPrecision(Integer.parseInt(args[i + 1]));
            }
            else if (args[i].equals("-g"))
            {
                settings.setGeographicPrecision(Integer.parseInt(args[i + 1]));
            }
            else if (args[i].equals("-p"))
            {
                settings.setDatastoragePrecision(Integer.parseInt(args[i + 1]));
            }
        }
    }

}
