package logisticsmarshall.tqs.ua.utils;

import logisticsmarshall.tqs.ua.model.Delivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static List<Map<String, String>> getStateMapList(List<Delivery> deliveries) {
        List<Map<String,String>> infoList = new ArrayList<>();
        for(Delivery del : deliveries){
            Map<String,String> infoDelivery = new HashMap<>();
            infoDelivery.put("id",String.valueOf(del.getId()));
            infoDelivery.put("state",del.getStage().name());
            infoList.add(infoDelivery);
        }
        return infoList;
    }
    private Utils(){
        //exists so this class can't be instanced
    }
}
