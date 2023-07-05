package common.response;

import javax.xml.bind.annotation.XmlRootElement;

import common.CommonCleaningRobot;

import java.util.List;

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
