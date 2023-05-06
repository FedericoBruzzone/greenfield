package administrator.server.beans.robot;

public class CleaningRobot implements ICleaningRobot {
    private int ID;
    private int district; 

    public CleaningRobot() {}

    public CleaningRobot(int ID, int district) {
        this.ID = ID;
        this.district = district;
    }

    public int getID() {
        return this.ID;
    }

    public int getDistrict() {
        return this.district;
    }

}
