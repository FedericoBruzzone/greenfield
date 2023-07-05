package administrator.server.beans.robot;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import simulator.Measurement;
import common.CommonCleaningRobot;

/**
 * This class is a singleton class that contains a list of CommonCleaningRobot objects.
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public final class CommonCleaningRobots implements ICommonCleaningRobots {
    
    private static CommonCleaningRobots instance;
    private List<CommonCleaningRobot> cleaningRobotsList;
    private final HashMap<Integer, ArrayList<Measurement>> measurementsMap;
    
    /**
     * It is private to prevent the instantiation of the class from outside the class itself.
     */
    private CommonCleaningRobots() {
        cleaningRobotsList = new ArrayList<CommonCleaningRobot>();
        measurementsMap = new HashMap<Integer, ArrayList<Measurement>>();
    }
    
    /**
     * This method returns the instance of the class.
     * @return the instance of the class
     */
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
  
    /**
     * This method adds a CommonCleaningRobot object to the list.
     *
     * @param cleaningRobot the CommonCleaningRobot object to add
     * @return the CommonCleaningRobot object added
     */
    public CommonCleaningRobot add(CommonCleaningRobot cleaningRobot) {
        synchronized(cleaningRobotsList) {
            if (!checkID(cleaningRobot.getId())) {
                // throw new RuntimeException("Failed: There is another Cleaning Robot with this ID "+cleaningRobot.getId());
                return null;
            }
            cleaningRobot.setDistrict(buildDistrict());
            cleaningRobotsList.add(cleaningRobot);
            return cleaningRobot;
        }
    }
   
    /**
     * This method removes a CommonCleaningRobot object from the list.
     *
     * @param cleaningRobot the CommonCleaningRobot object to remove
     * @return true if the CommonCleaningRobot object has been removed, false otherwise
     */
    public synchronized Boolean remove(CommonCleaningRobot cleaningRobot) {
        synchronized(cleaningRobotsList) {
            if (cleaningRobot == null) {
                return false;
            }
            int lengthCleaningRobotsList = cleaningRobotsList.size();
            cleaningRobotsList.removeIf(cr -> cr.getId() == cleaningRobot.getId());
            return lengthCleaningRobotsList - 1 == cleaningRobotsList.size();
        }
    }
    
    /**
     * This method returns the list of active CommonCleaningRobot.
     *
     * @return the list of active CommonCleaningRobot
     */
    public List<CommonCleaningRobot> getCleaningRobots() {
        synchronized(cleaningRobotsList) {
            if (cleaningRobotsList != null)
                return new ArrayList<CommonCleaningRobot>(cleaningRobotsList);
            return null;
        }
    }
  
    /** 
     * This method returns the list of CommonCleaningRobot without the specified commonCleaningRobot.
     *
     * @param commonCleaningRobot the CommonCleaningRobot object to exclude
     * @return the list of CommonCleaningRobot without the specified commonCleaningRobot
     */
    public List<CommonCleaningRobot> getCleaningRobotsWithout(CommonCleaningRobot commonCleaningRobot) {
        synchronized(cleaningRobotsList) {
            if (cleaningRobotsList != null && commonCleaningRobot != null)
                return new ArrayList<CommonCleaningRobot>(cleaningRobotsList.stream()
                                                                            .filter(cleaningRobot -> cleaningRobot.getId() != commonCleaningRobot.getId())
                                                                            .collect(Collectors.toList()));
            return null;
        }
    }

    /**
     * This method return the district number with less cleaning robots.
     *
     * @return the district number with less cleaning robots
     */
    public int buildDistrict() {
        return CommonCleaningRobots.getInstance()
                                   .getDistricts()
                                   .stream()
                                   .reduce(0L, (count, n) -> count + 1, Long::min)
                                   .intValue() % 4;
    }

    /**
     * This method returns the list of districts.
     *
     * @return the list of districts
     */
    public List<Integer> getDistricts() {
        return cleaningRobotsList.stream()
                                 .map(cleaningRobot -> cleaningRobot.getDistrict())
                                 .distinct()
                                 .collect(Collectors.toList());    
    }

    /**
     * This method set the list of CommonCleaningRobot.
     *
     * @param cleaningRobotsList the list of CommonCleaningRobot to set
     */
    public void setCleaningRobots(List<CommonCleaningRobot> cleaningRobotsList) {
        synchronized(cleaningRobotsList) {
            if (cleaningRobotsList.stream()
                                  .anyMatch(cleaningRobot -> cleaningRobotsList.stream()
                                                                               .filter(cr -> cr.getId() == cleaningRobot.getId())
                                                                               .count() > 1)) {
                throw new RuntimeException("Failed: There are duplicate Cleaning Robots in the list");
            }
            this.cleaningRobotsList = cleaningRobotsList;
        }
    }
   
    /**
     * This method checks if there is another CommonCleaningRobot with the same ID.
     *
     * @param ID the ID to check
     * @return true if there is not another CommonCleaningRobot with the same ID, false otherwise
     */
    private boolean checkID(int ID) {
        return !cleaningRobotsList.stream()
                                  .anyMatch(cleaningRobot -> cleaningRobot.getId() == ID);
    }
   
    /**
     * This method adds a measurement to the map of measurements of the specified robot.
     *
     * @param robotId the ID of the robot
     * @param measurements the measurements to add
     */
    public void addMeasurementWithId(int robotId, ArrayList<Measurement> measurements) {
        synchronized(measurementsMap) {
            if (!measurementsMap.containsKey(robotId)) {
                measurementsMap.put(robotId, new ArrayList<Measurement>());
            }
            measurementsMap.get(robotId).addAll(0, measurements);
        }
    }
    
    /**
     * This method returns the average of the first n measurements of the specified robot.
     *
     * @param robotId the ID of the robot
     * @param number the number of measurements to consider
     * @return the average of the first n measurements of the specified robot
     */
    public float getAverageOfLastNAirPollutionLevelsOfRobot(int robotId, int number) {
        synchronized(measurementsMap) {
            if (!measurementsMap.containsKey(robotId)) {
                return 0.0f;
            }
            if (measurementsMap.get(robotId).size() < number) {
                return 0.0f;
            }

            return measurementsMap.get(robotId)
                                  .stream()
                                  .limit(number)
                                  .map(measurement -> measurement.getValue())
                                  .reduce(0.0, (a, b) -> a + b)
                                  .floatValue() / number;
        }
    }

    /**
     * This method returns the average of the measurements of all robots between the specified timestamps.
     *
     * @param from the starting timestamp
     * @param to the ending timestamp
     * @return the average of the measurements of all robots between the specified timestamps
     */
    public synchronized float getAverageOfAirPollutionLevelsOfAllRobotsBetween(long from, long to) {
        if (measurementsMap.isEmpty()) {
            return 0.0f;
        }
        return measurementsMap.values()
                              .stream()
                              .flatMap(measurements -> measurements.stream())
                              .filter(measurement -> measurement.getTimestamp() >= from && measurement.getTimestamp() <= to)
                              .map(measurement -> measurement.getValue())
                              .reduce(0.0, (a, b) -> a + b)
                              .floatValue() / measurementsMap.values()
                                                             .stream()
                                                             .flatMap(measurements -> measurements.stream())
                                                             .filter(measurement -> measurement.getTimestamp() >= from && measurement.getTimestamp() <= to)
                                                             .count(); 
    }

}
