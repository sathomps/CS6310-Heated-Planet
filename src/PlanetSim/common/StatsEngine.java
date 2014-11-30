package PlanetSim.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import PlanetSim.common.event.EventBus;
import PlanetSim.common.event.PrintEvent;
import PlanetSim.common.event.StopEvent;
import PlanetSim.common.event.Subscribe;
import PlanetSim.display.DisplayEvent;
import PlanetSim.model.GridCell;

public class StatsEngine {
	static LinkedList<StatsData> allData = new LinkedList<StatsData>();
	private final AtomicBoolean doPrint           = new AtomicBoolean(true);
	private static int header = 0;
	SimulationSettings settings;

	public StatsEngine (EventBus bus) {
	bus.subscribe(this);
	}
	/**
	 * calcs the max, min and mean temp of the cells in teh query window
	 * also returns an arraylist of Doubles ordered left to right of the columns 
	 * in the grid with their average temperatures
	 * @param settings - data set just queried or created
	 * @return StatsData class
	 */
	public StatsData aggregate(SimulationSettings settings)
	{
		StatsData result = new StatsData();
		this.settings = settings;
		Double acctemp  = 0.0;
		Double cnt = 0.0;
		int print = 1;
		//note that this is a column first traversal of the grid.  This is because
		//the mean of the same timezone is required.  that meant column first. 
        final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();

        Date t1 = settings.getSimulationTimestamp().getTime() ;
        Date t2 = settings.getSimulationStartDate().getTime();
        
        if (print == 1)
        	System.out.print(settings.getSimulationTimestamp().getTime());
        
        for (int col = 0; col < grid.get(0).size(); col++)
        {
        	Double timeTemp = 0.0;
        	int row = 0; //scoped outside the for because it is used after the for
        	//for the mean temp of the column.
            for (; row < grid.size(); row++)
            {
            	final GridCell c = grid.get(row).get(col);
            	//only want the temp if it is in the queried window
            	//if (isInRectangle(settings.getLongitudeLeft(), settings.getLongitudeRight(), settings.getLatitudeTop(), settings.getLatitudeBottom()
            	//		, c.getLongitudeLeft(), c.getLatitudeTop()))
            	{
            		
            		if (result.getMinTemp() > c.getTemp())
            			result.setMinTemp(c.getTemp());
            		if (result.getMaxTemp() < c.getTemp())
            			result.setMaxTemp(c.getTemp());
            		acctemp += c.getTemp();
            		timeTemp += c.getTemp();
            		cnt++;
            		if (doPrint.get() && (print == 1))
            		{
	            		System.out.print("\t");
	            		System.out.print(String.format("(%3.7f)", c.getTemp()));
            		}
            	}
            }
        }
        
        if ((doPrint.get() == true) && (print == 1) && settings.getMeanRegionTemp() > 0.)
		{
        	System.out.print(String.format("\t(Mean = %3.7f)", acctemp/(grid.get(0).size() *grid.size())));
		   //result.setMeanTemp(acctemp / cnt);
        	System.out.println();
    		allData.add(result);
		}
		return result;
	}
	
	public double getMaxTemp()
	{
		double maxTemp = 0;
		
		 for (int inx = 0; inx < allData.size(); inx++)
         {
         	final StatsData c = allData.get(inx);
         	if (inx ==0)
         	{
         		maxTemp = c.getMaxTemp();
         	}
         	else if (maxTemp < c.getMaxTemp())
         	{
         		maxTemp = c.getMaxTemp();
         	}
         }
		 
		 return maxTemp;
	}
	
	public double getMinTemp()
	{
		double minTemp = 0;
		
		 for (int inx = 0; inx < allData.size(); inx++)
        {
        	final StatsData c = allData.get(inx);
        	if (inx ==0)
        	{
        		minTemp = c.getMinTemp();
        	}
        	else if (minTemp > c.getMinTemp())
        	{
        		minTemp = c.getMinTemp();
        	}
        }
		 
		 return minTemp;
	}
	
	 @Subscribe
    public void process(final PrintEvent event)
    {
		 if (doPrint.get() && settings.getMaxTemp() > 0.)
		 {
			 System.out.print("Max=");
			 System.out.println(String.format("(%3.7f)",getMaxTemp()));
		 }
		 if (doPrint.get() && settings.getMinTemp() > 0.)
		 {
			 System.out.print("Min=");
			 System.out.println(String.format("(%3.7f)",getMinTemp()));
		 }
		 
		 if (doPrint.get())
		 {
			 System.out.println("Storage Precision = " + settings.getDatastoragePrecision()); //int
			 System.out.println("Geographic Precision = " + settings.getGeographicPrecision() + "%"); //int
			 System.out.println("Temporal Precision = " + settings.getTemporalPrecision() + "%"); //int
		 }
		 doPrint.set(false);
    }
	
	
    private boolean isInRectangle(final double longitudeLeft, final double longitudeRight, final double latitudeTop, final double latitudeBottom,
            final double x, final double y)
    {
		//if zero was supplied for all four corners then the whole grid is what we want - return true
		if ((longitudeLeft != 0) && (latitudeBottom  != 0)&& (longitudeRight != 0) && (latitudeTop  != 0))
			return (longitudeLeft <= x) && (x <= longitudeRight) && (latitudeTop >= y) && (latitudeBottom <= y);
		else 
			return true;
    }
	public class StatsData
	{
		private double maxTemp = 0;
		private double minTemp = 100;
		private double meanTemp = 0;
		private ArrayList<Double> timeTemp ;
		
		public StatsData() 
		{
			timeTemp = new ArrayList<Double>();
		}
		public double getMaxTemp() {
			return maxTemp;
		}
		public void setMaxTemp(double maxTemp) {
			this.maxTemp = maxTemp;
		}
		public double getMinTemp() {
			return minTemp;
		}
		public void setMinTemp(double minTemp) {
			this.minTemp = minTemp;
		}
		public double getMeanTemp() {
			return meanTemp;
		}
		public void setMeanTemp(double meanTemp) {
			this.meanTemp = meanTemp;
		}
		public ArrayList<Double> getTimeTemp() {
			return timeTemp;
		}
		public void setTimeTemp(ArrayList<Double> timeTemp) {
			this.timeTemp = timeTemp;
		}
		
	}
}
