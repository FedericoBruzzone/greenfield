package administrator.server.beans.robot;

public class CleaningRobot implements ICleaningRobot {
    private final int ID;
    private String district; 

    public CleaningRobot(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return this.ID;
    }

    public String getDistrict() {
        return this.district;
    }
    // main
    // instance of server (connect to server)
    // chiedo di entrare
    // prendo la risposta e setto il distretto per usarla come topic mqtt
    // start all threads
    //     accendi sensori
    //     saluti gli altri (per aggiungerti alla loro lista)
    //     connettiti a mqtt

}
