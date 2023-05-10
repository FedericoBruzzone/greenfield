package administrator.server.beans.robot;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Collections;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.SystemParameter;

import common.CommonICleaningRobot;
import common.CommonCleaningRobot;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CleaningRobots implements ICleaningRobots {
    
    private static CleaningRobots instance;
    private List<CommonCleaningRobot> cleaningRobotsList;
    
    private CleaningRobots() {
        cleaningRobotsList = new ArrayList<CommonCleaningRobot>();
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
   
    public synchronized List<CommonCleaningRobot> getCleaningRobots() {
        return new ArrayList<CommonCleaningRobot>(cleaningRobotsList);
    }
   
    public synchronized List<CommonCleaningRobot> getCleaningRobotsWithout(CommonCleaningRobot commonCleaningRobot) {
        return new ArrayList<CommonCleaningRobot>(cleaningRobotsList.stream()
                                                                    .filter(cleaningRobot -> cleaningRobot.getId() != commonCleaningRobot.getId())
                                                                    .collect(Collectors.toList()));
    }

    public synchronized CommonCleaningRobot add(CommonCleaningRobot cleaningRobot) {
        if (!checkID(cleaningRobot.getId())) {
            throw new RuntimeException("Failed: There is another Cleaning Robot with this ID "+cleaningRobot.getId());
        }
        cleaningRobot.setDistrict(buildDistrict());
        cleaningRobotsList.add(cleaningRobot);
        return cleaningRobot;
    }

    public int buildDistrict() {
        return CleaningRobots.getInstance()
                      .getDistricts()
                      .stream()
                      .reduce(0L, (count, n) -> count + 1, Long::min)
                      .intValue() % 4;
    }

    public List<Integer> getDistricts() {
        return cleaningRobotsList.stream()
                                 .map(cleaningRobot -> cleaningRobot.getDistrict())
                                 .distinct()
                                 .collect(Collectors.toList());    
    }

    public synchronized void setCleaningRobots(List<CommonCleaningRobot> cleaningRobotsList) {
        // check if all ID are unique
        this.cleaningRobotsList = cleaningRobotsList;
    }
    
    private boolean checkID(int ID) {
        return !cleaningRobotsList.stream().anyMatch(cleaningRobot -> cleaningRobot.getId() == ID);
    }

}
