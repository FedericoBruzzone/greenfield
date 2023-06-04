package administrator.server.beans.robot;

import java.util.List;
import java.util.ArrayList;

import simulator.Measurement;
import common.CommonCleaningRobot;

public interface ICommonCleaningRobots {
    public List<CommonCleaningRobot> getCleaningRobots();
    public List<CommonCleaningRobot> getCleaningRobotsWithout(CommonCleaningRobot cleaningRobot);
    public CommonCleaningRobot add(CommonCleaningRobot cleaningRobot);
    public Boolean remove(CommonCleaningRobot cleaningRobot);
    public List<Integer> getDistricts();
    public void setCleaningRobots(List<CommonCleaningRobot> cleaningRobots);
    public void addMeasurementWithId(int id, ArrayList<Measurement> measurements);
}
