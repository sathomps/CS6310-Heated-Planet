package EarthSim.metrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.management.OperatingSystemMXBean;

public class MetricsEngine implements Runnable
{
    private static final SimpleDateFormat SDF      = new SimpleDateFormat("hh:mm:ss.SSS");
    private static final String           LINE_SEP = System.getProperty("line.separator");

    @Override
    public void run()
    {
        new MetricsTimer();
    }

    private void outputMetricsHeader(final Writer writer) throws IOException
    {
        final StringBuilder line = new StringBuilder();
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

    private void outputMetrics(final Writer writer) throws IOException
    {
        final OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        final StringBuilder line = new StringBuilder();
        line.append(bean.getCommittedVirtualMemorySize());
        line.append(",");
        line.append(bean.getFreePhysicalMemorySize());
        line.append(",");
        line.append(bean.getFreeSwapSpaceSize());
        line.append(",");
        line.append(bean.getProcessCpuLoad());
        line.append(",");
        line.append(bean.getProcessCpuTime());
        line.append(",");
        line.append(bean.getSystemCpuLoad());
        line.append(",");
        line.append(bean.getTotalPhysicalMemorySize());
        line.append(",");
        line.append(bean.getTotalSwapSpaceSize());
        line.append(",");
        line.append(SDF.format(new Date()));
        writer.append(line);
        writer.append(LINE_SEP);
    }

    private class MetricsTimer extends Timer
    {
        public MetricsTimer()
        {
            scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    Writer writer = null;
                    try
                    {
                        writer = new OutputStreamWriter(new FileOutputStream("metrics.csv"), "utf-8");

                        outputMetricsHeader(writer);
                        outputMetrics(writer);
                    }
                    catch (final IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                    finally
                    {
                        try
                        {
                            writer.close();
                        }
                        catch (final IOException e)
                        {
                        }
                    }
                }
            }, 0, MILLISECONDS.convert(500, MILLISECONDS));
        };
    }
}
