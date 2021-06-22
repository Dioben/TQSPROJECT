package logisticsmarshall.tqs.ua.model;

import lombok.Data;

@Data
public class DriverAdminView {
    long id;
    String name;
    String email;
    String vehicle;
    String phoneNo;
    double rating;
    long reviewcount;

     public DriverAdminView(long id, String name, String email, Driver.Vehicle vehicle, String phoneNo, double rating, long reviewcount){
        this.id = id;
        this.name = name;
        this.email = email;
        this.vehicle = vehicle.toString();
        this.phoneNo = phoneNo;
        this.rating = rating;
        this.reviewcount = reviewcount;

    }
}
