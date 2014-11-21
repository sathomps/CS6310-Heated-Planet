package PlanetSim.metrics;

public class MetricProducedEvent {
	private String source = "";
	private double  value = 0;
	public MetricProducedEvent (String source, double value)
	{
		this.source = source;
		this.value = value;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
