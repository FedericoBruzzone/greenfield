package robot;

public class CleaningRobotInfo {
    public int id;
    public String host;
    public String port;
    public int district;
    public long timestamp;    

    public CleaningRobotInfo(int id, String host, String port, int district) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.district = district;
        this.timestamp = 0;
    }
    
    public CleaningRobotInfo(int id, String host, String port, int district, long timestamp) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.district = district;
        this.timestamp = timestamp;
    }    

    public String toString() {
        return Integer.toString(this.id) + " " + this.host + " " + this.port + " " + Integer.toString(this.district);
    }
}
