package administrator.server.beans.robot.response;

import javax.xml.bind.annotation.XmlRootElement;

import administrator.server.beans.robot.ICleaningRobot;
import administrator.server.beans.robot.CleaningRobots;

import java.util.List;

@XmlRootElement
public class RobotAddResponse implements IResponse {
    private List<ICleaningRobot> listActiveCleaningRobot;
    private int district;

    public RobotAddResponse() {
        this.listActiveCleaningRobot = CleaningRobots.getInstance().getCleaningRobots();
        this.district = getDistrict();
    }

    public int getDistrict() {
        return CleaningRobots.getInstance()
                             .getDistricts()
                             .stream()
                             .min(Integer::compare)
                             .get(); 
    }
}
