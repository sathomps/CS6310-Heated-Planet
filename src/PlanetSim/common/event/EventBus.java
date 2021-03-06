package PlanetSim.common.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class EventBus
{
    private static final AtomicReference<EventBus> INSTANCE                = new AtomicReference<EventBus>();

    private final Map<Class<?>, List<Method>>      eventsSubscriberMethods = new HashMap<Class<?>, List<Method>>();
    private final Map<Method, Object>              eventsSubscribers       = new HashMap<Method, Object>();

    private boolean                                synchronizedCalls       = false;

    private EventBus(final boolean synchronizedCalls)
    {
        this.synchronizedCalls = synchronizedCalls;
    }

    public static EventBus getInstance()
    {
        return getInstance(false);
    }

    public static EventBus getInstance(final boolean synchronizedCalls)
    {
        INSTANCE.compareAndSet(null, new EventBus(synchronizedCalls));
        return INSTANCE.get();
    }

    public void subscribe(final Object subscriber)
    {
        storeSubscriberMethod(subscriber);
    }

    public void publish(final Object event)
    {
        if (eventsSubscriberMethods.containsKey(event.getClass()))
        {
            for (final Method subscriber : eventsSubscriberMethods.get(event.getClass()))
            {
                publish(subscriber, event);
            }
        }
    }

    private void publish(final Method subscriber, final Object event)
    {
        try
        {
            if (synchronizedCalls)
            {
                subscriber.invoke(eventsSubscribers.get(subscriber), event);
            }
            else
            {
                new Thread(new AsynchronousInvocation(subscriber, event)).start();
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    private void storeSubscriberMethod(final Object subscriber)
    {
        for (final Method method : subscriber.getClass().getDeclaredMethods())
        {
            if (method.isAnnotationPresent(Subscribe.class))
            {
                if ((method.getParameterTypes().length == 1))
                {
                    final Class<?> eventType = method.getParameterTypes()[0];
                    List<Method> subscriberMethods = eventsSubscriberMethods.get(eventType);

                    if (subscriberMethods == null)
                    {
                        subscriberMethods = new ArrayList<Method>();
                    }
                    subscriberMethods.add(method);
                    eventsSubscriberMethods.put(eventType, subscriberMethods);

                    eventsSubscribers.put(method, subscriber);
                }
            }
        }
    }

    private class AsynchronousInvocation implements Runnable
    {
        private final Method subscriber;
        private final Object event;

        public AsynchronousInvocation(final Method subscriber, final Object event)
        {
            this.subscriber = subscriber;
            this.event = event;
        }

        @Override
        public void run()
        {
            try
            {
                subscriber.invoke(eventsSubscribers.get(subscriber), event);
            }
            catch (final Exception e)
            {
                e.printStackTrace();
                System.exit(1);
                throw new RuntimeException(e);
            }
        }
    }

}
