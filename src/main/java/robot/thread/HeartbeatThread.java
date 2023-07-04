package robot.thread;

import robot.CleaningRobot;
import robot.grpc.HeartbeatServiceClient;

import java.lang.Thread;
import java.util.ArrayList;

public class HeartbeatThread extends Thread {

    private CleaningRobot cleaningRobot;
    private volatile Boolean stop;

    public HeartbeatThread(CleaningRobot cleaningRobot) {
        this.cleaningRobot = cleaningRobot;
        this.stop = false;
    }

    public void stopMeGently() {
        this.stop = true;
    }

    public void run() {
        while (!stop) {
            try {
                Thread.sleep(1000);           
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.cleaningRobot.sendHeartbeatToAll(); 
        }
    }
}
