package robot;

public class CleaningRobotInfo {
    public int id;
    public String host;
    public String port;

    public CleaningRobotInfo(int id, String host, String port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String toString() {
        return Integer.toString(this.id) + " " + this.host + " " + this.port;
    }
}
