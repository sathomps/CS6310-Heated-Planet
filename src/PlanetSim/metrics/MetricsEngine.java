package PlanetSim.metrics;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.RunEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.db.DBEngine;
import PlanetSim.simulation.SimulationEngineDaemon;

import com.sun.management.OperatingSystemMXBean;

public class MetricsEngine
{
    private static final SimpleDateFormat      SDF      = new SimpleDateFormat("hh:mm:ss.SSS");
    private static final String                LINE_SEP = System.getProperty("line.separator");

    private final EventBus                     eventBus;

    private Writer                             writer;

    private static final OperatingSystemMXBean OS_BEAN  = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public MetricsEngine(final EventBus eventBus)
    {
        this(eventBus, new Date().getTime() + "");
    }

    public MetricsEngine(final EventBus eventBus, final String fileName)
    {
        this.eventBus = eventBus;
        this.eventBus.subscribe(this);
        try
        {
            writer = new OutputStreamWriter(new FileOutputStream("metrics/metrics_" + fileName + ".csv"), "utf-8");
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
            final double value = OS_BEAN.getProcessCpuLoad();

            if (value > 0)
            {
                return value * 100.00;
            }
        }
        catch (final Exception e)
        {
        }
        return 0.0;
    }

    public static void main(final String[] args)
    {
        int test = 1;
        for (final SimulationSettings settings : buildTests())
        {
            for (int testIteration = 1; testIteration <= 5; ++testIteration)
            {
                final String fileName = "test_" + test + "_iteration_" + testIteration;
                final String simulationName = "test_" + test + "_iteration_" + testIteration + "_" + System.nanoTime();

                settings.setSimulationName(simulationName);
                final EventBus eventBus = EventBus.getInstance(true);
                new DBEngine(eventBus);
                new SimulationEngineDaemon(eventBus);
                new MetricsEngine(eventBus, fileName);

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        eventBus.publish(new RunEvent(settings));
                    }
                }).start();
            }
            ++test;
        }
    }

    private static List<SimulationSettings> buildTests()
    {
        final LinkedList<SimulationSettings> tests = new LinkedList<SimulationSettings>();
        try
        {
            final BufferedReader br = new BufferedReader(new InputStreamReader(MetricsEngine.class.getResourceAsStream("tests.csv")));
            String line = null;
            while ((line = br.readLine()) != null)
            {
                final SimulationSettings settings = new SimulationSettings();
                final String[] params = line.split(",");

                settings.setPlanetsAxialTilt(Double.parseDouble(params[0]));
                settings.setPlanetsOrbitalEccentricity(Double.parseDouble(params[1]));
                settings.setGridSpacing(Integer.parseInt(params[2]));
                settings.setSimulationTimeStepMinutes(Integer.parseInt(params[3]));
                settings.setSimulationLength(Integer.parseInt(params[4]));
                settings.setDatastoragePrecision(Integer.parseInt(params[5]));
                settings.setGeographicPrecision(Integer.parseInt(params[6]));
                settings.setTemporalPrecision(Integer.parseInt(params[7]));

                tests.add(settings);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }

        return tests;
    }
}
