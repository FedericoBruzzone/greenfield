package administrator.server.beans.robot.response;

import javax.xml.bind.annotation.XmlRootElement;

import administrator.server.beans.robot.ICleaningRobot;

import java.util.List;

@XmlRootElement
public class RobotAddResponse implements IResponse {
    private List<ICleaningRobot> listCleaningRobot;
    private String district;

    public RobotAddResponse(List<ICleaningRobot> listCleaningRobot) {
        this.listCleaningRobot = listCleaningRobot;
        district = generateDistrict();
    }

    public String generateDistrict() {
           
    }
}
