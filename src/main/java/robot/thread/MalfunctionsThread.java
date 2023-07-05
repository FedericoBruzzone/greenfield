package robot.thread;

import java.lang.Thread;

import robot.CleaningRobot;

/**
 * This class implements a thread that simulates the robot's malfunctions.
 */
public class MalfunctionsThread extends Thread {
     
    private CleaningRobot cleaningRobot;
    private volatile Boolean needFix = false;
    private volatile Boolean stop;

    /**
     * Constructor a new MalfunctionsThread.
     * @param cleaningRobot The robot that will be used.
     */
    public MalfunctionsThread(CleaningRobot cleaningRobot) {
        this.cleaningRobot = cleaningRobot;
        this.stop = false;
    } 
 
    /**
     * This method sets the needFix attribute.
     */
    public void setNeedFix(Boolean needFix) {
        this.needFix = needFix;
        this.interrupt();
    }
        
    /**
     * This method stops the thread.
     */
    public void stopMeGently() {
        this.stop = true;
    }

    /**
     * This method simulates the robot's malfunctions.
     */
    public void run() {
        while (!stop) {
            try {
                Thread.sleep(1000 * 10);           
            } catch (InterruptedException e) {
                // System.out.println("[MalfunctionsThread]: Interrupted, need mechanical");
                // e.printStackTrace();
            }
            int randomNum = 1 + (int)(Math.random() * 10);
            System.out.println("Random num: " + randomNum);

            if (randomNum % 2 == 0 || needFix) {
                cleaningRobot.setIsBroken(true);
                cleaningRobot.sendImBrokenToAll();
                System.out.println("[MalfunctionsThread]: Robot is broken");

                while(true) {           
                    Boolean allTrue = cleaningRobot.getResponseCleaningRobotsISentThatImBroken()
                                                   .values()
                                                   .stream()
                                                   .allMatch(Boolean::booleanValue);
                    // System.out.println("allTrue: " + allTrue);
                    // System.out.println("ALL: " + cleaningRobot.getResponseCleaningRobotsISentThatImBroken());
                    if (allTrue || cleaningRobot.getResponseCleaningRobotsISentThatImBroken().isEmpty()) {
                        try {
                            System.out.println("[MalfunctionsThread]: Mechanic repairs the robot");
                            cleaningRobot.setImAtTheMechanic(true);
                            Thread.sleep(1000 * 10);          
                        } catch (InterruptedException e) {
                            // e.printStackTrace();
                        }
                        this.setNeedFix(false);
                        cleaningRobot.setIsBroken(false);
                        cleaningRobot.setImAtTheMechanic(false);
                        cleaningRobot.sendImFixedToCleaningRobotsWithTimestampGreaterThanMineAll();
                        break;    
                    }
                    if (cleaningRobot.getIsBroken()) {
                        synchronized(this) {
                            try {
                                System.out.println("[MalfunctionsThread]: Waiting room");
                                this.wait();
                            } catch (InterruptedException e) {
                                // e.printStackTrace();
                            }
                        }
                    }
                }
            } 
        }
    }
}
