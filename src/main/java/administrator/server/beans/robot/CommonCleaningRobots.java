package administrator.server.beans.robot;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.api.SystemParameter;

import common.ICommonCleaningRobot;
import common.CommonCleaningRobot;

@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CommonCleaningRobots implements ICommonCleaningRobots {
    
    private static CommonCleaningRobots instance;
    private List<CommonCleaningRobot> cleaningRobotsList;
    private HashMap<Integer, ArrayList<Double>> measurementsMap;

    private CommonCleaningRobots() {
        cleaningRobotsList = new ArrayList<CommonCleaningRobot>();
        measurementsMap = new HashMap<Integer, ArrayList<Double>>();
    }

    public static CommonCleaningRobots getInstance() {
       if (instance == null) {
            synchronized (CommonCleaningRobots.class) {
                if (instance == null) {
                    instance = new CommonCleaningRobots();
                }
            }
        }
        return instance;
    }
   
    public synchronized CommonCleaningRobot add(CommonCleaningRobot cleaningRobot) {
        if (!checkID(cleaningRobot.getId())) {
            // throw new RuntimeException("Failed: There is another Cleaning Robot with this ID "+cleaningRobot.getId());
            return null;
        }
        cleaningRobot.setDistrict(buildDistrict());
        cleaningRobotsList.add(cleaningRobot);
        return cleaningRobot;
    }
    
    public synchronized Boolean remove(CommonCleaningRobot cleaningRobot) {
        if (cleaningRobot == null) {
            return false;
        }
        cleaningRobotsList.removeIf(cr -> cr.getId() == cleaningRobot.getId());
        return true;
    }

    public synchronized List<CommonCleaningRobot> getCleaningRobots() {
        if (cleaningRobotsList != null)
            return new ArrayList<CommonCleaningRobot>(cleaningRobotsList);
        return null;
    }
   
    public synchronized List<CommonCleaningRobot> getCleaningRobotsWithout(CommonCleaningRobot commonCleaningRobot) {
        if (cleaningRobotsList != null && commonCleaningRobot != null)
            return new ArrayList<CommonCleaningRobot>(cleaningRobotsList.stream()
                                                                    .filter(cleaningRobot -> cleaningRobot.getId() != commonCleaningRobot.getId())
                                                                    .collect(Collectors.toList()));
        return null;
    }


    public int buildDistrict() {
        return CommonCleaningRobots.getInstance()
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
    
    public synchronized void addMeasurementWithId(int id, ArrayList<Double> measurements) {
        measurementsMap.put(id, measurements);
    }
}
