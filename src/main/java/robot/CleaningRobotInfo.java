package robot;

public class CleaningRobotInfo {
    public int id;
    public String host;
    public String port;
    public int district;

    public CleaningRobotInfo(int id, String host, String port, int district) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.district = district;
    }

    public String toString() {
        return Integer.toString(this.id) + " " + this.host + " " + this.port + " " + Integer.toString(this.district);
    }
}
