import com.opencsv.bean.CsvBindByName;

public class FlowRate {

    private int id;

    @CsvBindByName
    private long time;
    @CsvBindByName
    private double density;
    @CsvBindByName
    private double fluid_temperature;
    @CsvBindByName
    private double mass_flow;
    @CsvBindByName
    private double sensor_fault_flags;
    @CsvBindByName
    private String sensorId;
    @CsvBindByName
    private double volume_flow;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getFluid_temperature() {
        return fluid_temperature;
    }

    public void setFluid_temperature(double fluid_temperature) {
        this.fluid_temperature = fluid_temperature;
    }

    public double getMass_flow() {
        return mass_flow;
    }

    public void setMass_flow(double mass_flow) {
        this.mass_flow = mass_flow;
    }

    public double getSensor_fault_flags() {
        return sensor_fault_flags;
    }

    public void setSensor_fault_flags(double sensor_fault_flags) {
        this.sensor_fault_flags = sensor_fault_flags;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getVolume_flow() {
        return volume_flow;
    }

    public void setVolume_flow(double volume_flow) {
        this.volume_flow = volume_flow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FlowRate{" +
                "time=" + time +
                ", density=" + density +
                ", fluid_temperature=" + fluid_temperature +
                ", mass_flow=" + mass_flow +
                ", sensor_fault_flags=" + sensor_fault_flags +
                ", sensorId='" + sensorId + '\'' +
                ", volume_flow=" + volume_flow +
                '}';
    }
}
