package administrator.server.beans.robot;

import java.util.List;
import java.util.ArrayList;

import common.CommonICleaningRobot;
import common.CommonCleaningRobot;

public interface ICleaningRobots {
    public List<CommonCleaningRobot> getCleaningRobots();
    public List<CommonCleaningRobot> getCleaningRobotsWithout(CommonCleaningRobot cleaningRobot);
    public CommonCleaningRobot add(CommonCleaningRobot cleaningRobot);
    public Boolean remove(CommonCleaningRobot cleaningRobot);
    public List<Integer> getDistricts();
    public void setCleaningRobots(List<CommonCleaningRobot> cleaningRobots);
    public void addMeasurementWithId(int id, ArrayList<Double> measurements);
}
