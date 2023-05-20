package common;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement
public class CommonCleaningRobot implements ICommonCleaningRobot {
    private int id;
    private int district; 
    private String host;
    private String port;

    public CommonCleaningRobot() {}

    public CommonCleaningRobot(int id, String host, String port) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.district = -1;
    }

    public int getId() {
        return this.id;
    }
    
    public String getHost() {
        return this.host;
    }

    public String getPort() {
        return this.port;
    }

    public int getDistrict() {
        return this.district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "\n{\n" + 
            "\t" + "\"id\":" + this.id + ",\n" +
            "\t" + "\"host\":" + this.host + ",\n" +
            "\t" + "\"port\":" + this.port + ",\n" +
            "\t" + "\"district\":" + this.district + ",\n" +
        "}";
    }
}
