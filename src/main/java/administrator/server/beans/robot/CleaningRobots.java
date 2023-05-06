package administrator.server.beans.robot;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CleaningRobots implements ICleaningRobots {
    
    private static CleaningRobots instance;
    @XmlElement(name = "cleaningRobot")
    private List<ICleaningRobot> cleaningRobotsList;

    private CleaningRobots() {
        cleaningRobotsList = new ArrayList<ICleaningRobot>();
    }

    public static CleaningRobots getInstance() {
       if (instance == null) {
            synchronized (CleaningRobots.class) {
                if (instance == null) {
                    instance = new CleaningRobots();
                }
            }
        }
        return instance;
    }
   
    public synchronized List<ICleaningRobot> getCleaningRobots() {
        return new ArrayList<ICleaningRobot>(cleaningRobotsList);
    }
    
    public synchronized void add(ICleaningRobot cleaningRobot) {
        checkID(cleaningRobot.getID());
        cleaningRobotsList.add(cleaningRobot);
    }

    public List<Integer> getDistricts() {
        return cleaningRobotsList.stream()
                                 .map(cleaningRobot -> cleaningRobot.getDistrict())
                                 .distinct()
                                 .collect(Collectors.toList());    
    }

    public synchronized void setCleaningRobots(List<ICleaningRobot> cleaningRobotsList) {
        // check if all ID are unique
        this.cleaningRobotsList = cleaningRobotsList;
    }
    
    private boolean checkID(int ID) {
        return cleaningRobotsList.stream().anyMatch(cleaningRobot -> cleaningRobot.getID() == ID);
    }


}
