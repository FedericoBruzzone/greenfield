package common.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import administrator.server.beans.robot.CommonCleaningRobots;
import administrator.server.beans.robot.ICommonCleaningRobots;
import common.CommonCleaningRobot;
import common.ICommonCleaningRobot;

import java.util.List;
import java.util.ArrayList;

@XmlRootElement
// @XmlAccessorType(XmlAccessType.FIELD)
public class RobotAddResponse implements IResponse {
    public List<CommonCleaningRobot> listActiveCleaningRobot;
    public int district;
    
    public RobotAddResponse() {}

    public RobotAddResponse(List<CommonCleaningRobot> listActiveCleaningRobot, int distinct) {
        this.listActiveCleaningRobot = listActiveCleaningRobot;
        this.district = distinct;
    }

    public String toString() {
        return "RobotAddResponse{" +
                "listActiveCleaningRobot:" + listActiveCleaningRobot +
                ", district:" + district +
                "}";
    }
}
