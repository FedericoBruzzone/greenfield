package administrator.server.beans.robot;

import java.util.List;

import common.CommonICleaningRobot;
import common.CommonCleaningRobot;

public interface ICleaningRobots {
    public List<CommonCleaningRobot> getCleaningRobots();
    public CommonCleaningRobot add(CommonCleaningRobot cleaningRobot);
    public List<Integer> getDistricts();
    public void setCleaningRobots(List<CommonCleaningRobot> cleaningRobots);

}
