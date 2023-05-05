package administrator.server.beans.robot;

import java.util.List;

public interface ICleaningRobots {
    List<ICleaningRobot> getCleaningRobots();
    void add(ICleaningRobot cleaningRobot);
    void setCleaningRobots(List<ICleaningRobot> cleaningRobots);
}
