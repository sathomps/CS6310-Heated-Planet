package PlanetSim.common;

import java.util.ArrayList;
import java.util.LinkedList;

import PlanetSim.model.GridCell;

public class StatsEngine {

	public StatsEngine () {}
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
		Double acctemp  = 0.0;
		Double cnt = 0.0;
		//note that this is a column first traversal of the grid.  This is because
		//the mean of the same timezone is required.  that meant column first. 
        final LinkedList<LinkedList<GridCell>> grid = settings.getGrid();
        for (int col = 0; col < grid.get(0).size(); col++)
        {
        	Double timeTemp = 0.0;
        	int row = 0; //scoped outside the for because it is used after the for
        	//for the mean temp of the column.
            for (; row < grid.size(); row++)
            {
            	final GridCell c = grid.get(row).get(col);
            	//only want the temp if it is in the queried window
            	if (isInRectangle(settings.getLongitudeLeft(), settings.getLongitudeRight(), settings.getLatitudeTop(), settings.getLatitudeBottom()
            			, c.getLongitudeLeft(), c.getLatitudeTop()))
            	{
            		
            		if (result.getMinTemp() > c.getTemp())
            			result.setMinTemp(c.getTemp());
            		if (result.getMaxTemp() < c.getTemp())
            			result.setMaxTemp(c.getTemp());
            		acctemp += c.getTemp();
            		timeTemp += c.getTemp();
            		cnt++;
            	}
            }
            result.getTimeTemp().add(timeTemp / row);
        }
		result.setMeanTemp(acctemp / cnt);
		return result;
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
		private double minTemp = 0;
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
