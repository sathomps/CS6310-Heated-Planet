package EarthSim.common.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventBus
{
    private final List<Object> subscribers = new ArrayList<Object>();

    public void subscribe(final Object subscriber)
    {
        subscribers.add(subscriber);
    }

    public void publish(final Object event)
    {
        for (final Object subscriber : subscribers)
        {
            publish(subscriber, event);
        }
    }

    private void publish(final Object subscriber, final Object event)
    {
        for (final Method method : subscriber.getClass().getDeclaredMethods())
        {
            if (method.isAnnotationPresent(Subscribe.class))
            {
                if ((method.getParameterTypes().length == 1) && method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
                {
                    try
                    {
                        method.invoke(subscriber, event);
                    }
                    catch (final Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
