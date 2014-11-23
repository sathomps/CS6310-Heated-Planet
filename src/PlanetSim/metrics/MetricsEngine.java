package PlanetSim.metrics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

import PlanetSim.common.SimulationSettings;
import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.Subscribe;

import com.sun.management.OperatingSystemMXBean;

public class MetricsEngine
{
    private static final SimpleDateFormat      SDF      = new SimpleDateFormat("hh:mm:ss.SSS");
    private static final String                LINE_SEP = System.getProperty("line.separator");

    private final EventBus                     eventBus;
    private static final OperatingSystemMXBean OS_BEAN  = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private Writer                             writer;

    public MetricsEngine(final EventBus eventBus)
    {
        this.eventBus = eventBus;
        this.eventBus.subscribe(this);
        try
        {
            writer = new OutputStreamWriter(new FileOutputStream("metrics.csv"), "utf-8");
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
        line.append("SimulationTimestamp");
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
        line.append("CommittedVirtualMemorySize");
        line.append(",");
        line.append("FreePhysicalMemorySize");
        line.append(",");
        line.append("FreeSwapSpaceSize");
        line.append(",");
        line.append("ProcessCpuLoad");
        line.append(",");
        line.append("ProcessCpuTime");
        line.append(",");
        line.append("SystemCpuLoad");
        line.append(",");
        line.append("TotalPhysicalMemorySize");
        line.append(",");
        line.append("TotalSwapSpaceSize");
        line.append(",");
        line.append("Date");
        writer.append(line);
        writer.append(LINE_SEP);
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
            line.append(SDF.format(settings.getSimulationTimestamp().getTime()));
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
            line.append(OS_BEAN.getCommittedVirtualMemorySize());
            line.append(",");
            line.append(OS_BEAN.getFreePhysicalMemorySize());
            line.append(",");
            line.append(OS_BEAN.getFreeSwapSpaceSize());
            line.append(",");
            line.append(OS_BEAN.getProcessCpuLoad());
            line.append(",");
            line.append(OS_BEAN.getProcessCpuTime());
            line.append(",");
            line.append(OS_BEAN.getSystemCpuLoad());
            line.append(",");
            line.append(OS_BEAN.getTotalPhysicalMemorySize());
            line.append(",");
            line.append(OS_BEAN.getTotalSwapSpaceSize());
            line.append(",");
            line.append(SDF.format(new Date()));
            writer.append(line);
            writer.append(LINE_SEP);
        }
        catch (final Exception ex)
        {
        }
    }
}
