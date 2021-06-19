package logisticsmarshall.tqs.ua.model;

import lombok.Data;

@Data
public class DriverAdminView {
    long id;
    String vehicle;
    String phoneNo;
    double rating;
    long reviewcount;

     public DriverAdminView(long id, Driver.Vehicle vehicle, String phoneNo, double rating, long reviewcount){
        this.id = id;
        this.vehicle = vehicle.toString();
        this.phoneNo = phoneNo;
        this.rating = rating;
        this.reviewcount = reviewcount;

    }
}
