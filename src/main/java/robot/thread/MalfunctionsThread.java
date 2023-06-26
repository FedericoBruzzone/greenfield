package robot.thread;

import java.lang.Thread;

import robot.CleaningRobot;

/**
 * This class implements a thread that simulates the robot's malfunctions.
 */
public class MalfunctionsThread extends Thread {
     
    private CleaningRobot cleaningRobot;

    /**
     * Constructor a new MalfunctionsThread.
     * @param cleaningRobot The robot that will be used.
     */
    public MalfunctionsThread(CleaningRobot cleaningRobot) {
        this.cleaningRobot = cleaningRobot;
    } 
    
    /**
     * This method simulates the robot's malfunctions.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000); // * 10           
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int randomNum = 1 + (int)(Math.random() * 10);
            System.out.println("Random num: " + randomNum);
            if (randomNum == 1) {
                cleaningRobot.setIsBroken(true);
                cleaningRobot.sendImBrokenToAll();
                System.out.println("[MalfunctionsThread]: Robot is broken");

                Boolean allTrue = cleaningRobot.getResponseCleaningRobotsISentThatImBroken()
                                               .values()
                                               .stream()
                                               .allMatch(Boolean::booleanValue);
                
                System.out.println("allTrue: " + allTrue);

                if (cleaningRobot.getIsBroken() && allTrue) {
                    try {
                        // Mechanic repairs the robot
                        Thread.sleep(10000);          
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cleaningRobot.setIsBroken(false);
                    cleaningRobot.sendImFixedToCleaningRobotsWithTimestampGreaterThanMine();
                }
            } 
        }
    }
}
