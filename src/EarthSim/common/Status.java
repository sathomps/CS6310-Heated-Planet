package EarthSim.common;

import java.util.HashMap;
import java.util.Map;

public enum Status
{
    STOP, STOPPED, PAUSE, RUN;

    private static Map<String, Status> statuses = new HashMap<String, Status>();

    static
    {
        for (final Status status : values())
        {
            statuses.put(status.name().toLowerCase(), status);
        }
    }

    public static Status getStatus(final String status)
    {
        return statuses.get(status.toLowerCase());
    }
}
