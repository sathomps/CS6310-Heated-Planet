package PlanetSim.metrics;

import PlanetSim.common.SimulationSettings;

public class MetricEvent
{
    private long               databaseSize;
    private long               queryTime;
    private SimulationSettings settings;

    public long getDatabaseSize()
    {
        return databaseSize;
    }

    public MetricEvent setDatabaseSize(final long databaseSize)
    {
        this.databaseSize = databaseSize;
        return this;
    }

    public long getQueryTime()
    {
        return queryTime;
    }

    public MetricEvent setQueryTime(final long queryTime)
    {
        this.queryTime = queryTime;
        return this;
    }

    public SimulationSettings getSettings()
    {
        return settings;
    }

    public MetricEvent setSettings(final SimulationSettings settings)
    {
        this.settings = settings;
        return this;
    }
}
