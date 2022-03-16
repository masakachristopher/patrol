package wktechsys.com.guardprotection.Models;



public class IncidentModel {

    public Integer id;
    public String name;


    public IncidentModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public Integer toInt() {
        return id;
    }

}
