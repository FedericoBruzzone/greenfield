package robot.thread;

import robot.CleaningRobot;
import robot.grpc.HeartbeatServiceClient;

import java.lang.Thread;
import java.util.ArrayList;

public class HeartbeatThread extends Thread {

    private CleaningRobot cleaningRobot;

    public HeartbeatThread(CleaningRobot cleaningRobot) {
        this.cleaningRobot = cleaningRobot;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);           
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.cleaningRobot.sendHeartbeatToAll(); 
        }
    }
}
