package robot;

public class CleaningRobotInfo {
    public int id;
    // public String host;
    // public String port;

    public CleaningRobotInfo(int id) {
        this.id = id;
    }

    public String toString() {
        return Integer.toString(this.id);
    }
}
