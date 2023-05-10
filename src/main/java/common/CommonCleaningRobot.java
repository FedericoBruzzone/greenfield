package common;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement
public class CommonCleaningRobot implements CommonICleaningRobot {
    private int id;
    private int district; 

    public CommonCleaningRobot() {}

    public CommonCleaningRobot(int id) {
        this.id = id;
        this.district = -1;
    }

    public int getId() {
        return this.id;
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
        return "{\"id\":" + this.id + ",\"district\":" + this.district + "}";
    }
}
