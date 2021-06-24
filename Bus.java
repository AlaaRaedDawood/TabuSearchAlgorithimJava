package hackerank;

import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.text.DecimalFormat;

public class Bus {
	private static int bus_id = 1 ; 
	private int id ;
	private float lat ;
	private float lng ;
	private float currentlat ;
	private float currentlng ;
	private int startTime ;
	private int endTime ;
	private double busTime ;
	private int num_of_rides;
	private float wastedDistance ; 
	private float waitedTime ;
	private float final_distance_to_garage ;
	private ArrayList<Integer> rideList ;
	
	private static DecimalFormat df2 = new DecimalFormat("#.##");

	public Bus (float lat , float lng) {
		this.setLat(lat) ;
		this.setLng(lng);
		this.setCurrentlat(lat);
		this.setCurrentlng(lng);
		this.setStartTime(360) ; 
		this.setEndTime(1200) ;
		this.setBusTime(360);
		this.setNum_of_rides(0) ;
		rideList = new ArrayList<Integer>();
		this.id = bus_id ; 
		this.setWastedDistance(0); 
		this.setWaitedTime(0) ;
		this.setFinal_distance_to_garage(0);
		bus_id += 1 ;
		
	}
	public int get_id() {
		return id ;
	}

	public int getNum_of_rides() {
		return num_of_rides;
	}

	public void setNum_of_rides(int num_of_rides) {
		this.num_of_rides = num_of_rides;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public float getLng() {
		return lng;
	}

	public void setLng(float lng) {
       this.lng = lng;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}
	public ArrayList<Integer> getRideList() {
		return rideList;
	}
	public double getBusTime() {
		return busTime;
	}
	public void setBusTime(double busTime) {
		this.busTime = busTime;
	}
	public float getCurrentlat() {
		return currentlat;
	}
	public void setCurrentlat(float currentlat) {
		this.currentlat = currentlat;
	}
	public float getCurrentlng() {
		return currentlng;
	}
	public void setCurrentlng(float currentlng) {
		this.currentlng = currentlng;
	}
	public float getWastedDistance() {
		return wastedDistance;
	}
	public void setWastedDistance(float wastedDistance) {
		this.wastedDistance = wastedDistance;
	}
	public float getWaitedTime() {
		return waitedTime;
	}
	public void setWaitedTime(float waitedTime) {
		this.waitedTime = waitedTime;
	}
	public float getFinal_distance_to_garage() {
		return final_distance_to_garage;
	}
	public void setFinal_distance_to_garage(float final_distance_to_garage) {
		this.final_distance_to_garage = final_distance_to_garage;
	}
	public String return_rides () {
        String r = "" ;
        for(int i = 0 ; i < rideList.size() ; i++) {
        	r += rideList.get(i) + "," ;
        }
        if(r.length() > 0) {
        	r.substring(0, r.length()-1);
        }
        return r  ;
	}
	
	public static float getHaversineDistance (float lat1 , float lon1 , float lat2 , float lon2) {
		
        lon1 = (float) Math.toRadians(lon1);
        lon2 = (float) Math.toRadians(lon2);
        lat1 = (float) Math.toRadians(lat1);
        lat2 = (float) Math.toRadians(lat2);
 
        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.pow(Math.sin(dlon / 2),2);
             
        double c = 2 * Math.asin(Math.sqrt(a));

        double r = 6371;
       
        // calculate the result
        return Float.parseFloat(df2.format((c * r)));
    
    

}
	public void UpdateAttribute (ArrayList<Ride> rides) {
		this.wastedDistance = 0 ;
		this.waitedTime =  0;
		this.setCurrentlat(this.getLat());
		this.setCurrentlng(this.getLng());
		this.setBusTime(360);
        for(int i = 0 ; i < rideList.size() ; i++) {
        	int ride_id = rideList.get(i) -1 ;
        	float sd = 0;
			double timeTaken = 0 ;
			float arrival_time =  0 ;
			if(i == 0) {
			    sd = getHaversineDistance( rides.get(ride_id).getsLat() , rides.get(ride_id).getsLng() ,this.getLat() , this.getLng());
			    timeTaken = (sd/60)*60 ;
				arrival_time = (float) (this.getStartTime() + timeTaken) ;
			}else {
				sd = getHaversineDistance( rides.get(ride_id).getsLat() , rides.get(ride_id).getsLng() ,this.getCurrentlat() , this.getCurrentlng());
				timeTaken = (sd/60)*60 ;
				arrival_time = (float) (this.getBusTime() + timeTaken) ;
			}
			this.wastedDistance += sd ;
			if( i != 0) {
				this.waitedTime += (rides.get(ride_id).getSt()- arrival_time);
			}
			this.setCurrentlat(rides.get(ride_id).geteLat());
			this.setCurrentlng(rides.get(ride_id).geteLng());
			this.setBusTime(rides.get(ride_id).getEt());
        }
        float distanceToGarage = getHaversineDistance( this.getCurrentlat() , this.getCurrentlng() , this.getLat() , this.getLng());
        this.wastedDistance += distanceToGarage;
        this.final_distance_to_garage = distanceToGarage ;
        this.waitedTime = Float.parseFloat(df2.format(this.waitedTime));
        this.wastedDistance = Float.parseFloat(df2.format(this.wastedDistance));
	}
	public void addRide (int ride_id) {
        rideList.add(ride_id);
        
	}
	
	public void removeRide (int ride_id) {
        for(int i = 0 ; i < rideList.size() ; i++) {
        	if(ride_id == rideList.get(i)) {
        		rideList.remove(i);
        		break;
        	}
        }
        
	}

}



	