package administrator.server.beans.robot;

import java.util.List;

public interface ICleaningRobots {
    public List<ICleaningRobot> getCleaningRobots();
    public void add(ICleaningRobot cleaningRobot);
    public List<Integer> getDistricts();
    public void setCleaningRobots(List<ICleaningRobot> cleaningRobots);

}
