package hackerank;

import java.util.*;
import java.lang.*;
import java.text.DecimalFormat;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors

public class Main {
	public static int num_line = 0 ;
	public static int num_requests = 0 ;
	public static int num_buses = 0 ;
	public static ArrayList<Bus> buses = new ArrayList<>();
	public static ArrayList<Ride> requests = new ArrayList<>();
	public static ArrayList<Integer> tabuRequests = new ArrayList<>();
	public static Set<String> tabuMemory =  new HashSet<String>();
	public static ArrayList<Integer> tabuMemoryIteration = new ArrayList<>();
	public static String current_node = "" ;
	public static String previous_node = "" ;
	public static String path_of_rides = "" ;
	public static String current_bus_options = "" ;
	
	// decimal
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	//decrement tabuMemoryIteration
	public static void decrementTabuMemoryIteration () {
		for(int i = 0 ; i < tabuMemoryIteration.size() ; i ++) {
			int n = tabuMemoryIteration.get(i) - 1  ;
			if( n  > 0) {
				tabuMemoryIteration.set(i , n);
			}else {
				tabuMemoryIteration.remove(i);
				tabuMemory.remove(i);
			}
		}
}
	//checkOnBuses
	public static void UpdateOnBuses(int pre_assignedBus, int new_assignedBus, int ride_id) {
		if(pre_assignedBus != -1) {
			
			 buses.get(pre_assignedBus).removeRide(ride_id);
			 buses.get(pre_assignedBus).UpdateAttribute(requests);
		}
		if(new_assignedBus != -1) {
			 buses.get(new_assignedBus).addRide(ride_id);
			 buses.get(new_assignedBus).UpdateAttribute(requests);
		}
		
		
	}
	
	//checkMoveOnRide
	public static String checkMoveOnRide(float f , Ride r , int assigned_bus_id) {
		float max_f = 0 ;
		boolean start = true ;
		int bus_id = -1 ;
		for(int i = 0 ; i < buses.size() ; i ++) {
			float busLat = buses.get(i).getCurrentlat();
			float busLong = buses.get(i).getCurrentlng();
			float busGarageLat = buses.get(i).getLat();
			float busGarageLong = buses.get(i).getLng();
			float requestLat = r.getsLat();
			float requestLong = r.getsLng();
		    float sd = getHaversineDistance( requestLat , requestLong ,busLat , busLong);
		    double timeTaken = (sd/60)*60 ;
			float arrival_time = (float) (buses.get(i).getBusTime() + timeTaken) ;
			double busTimeAfterRide = r.getSt() + (r.getEt() - r.getSt());
			double distanceToGarage = getHaversineDistance( r.geteLat() , r.geteLng() , busGarageLat , busGarageLong);
			double timeTakenToGarage = (distanceToGarage/60) * 60 ;
			if((arrival_time <= r.getSt())&& ((busTimeAfterRide+timeTakenToGarage) <= buses.get(i).getEndTime()) ) {
				if((buses.get(i).getRideList().size() < 4 ) && (assigned_bus_id != i) && (!checkTabuMemory(r.get_id()-1 , i))) {
					float current_f = calculateF(i , r , assigned_bus_id);
					if(start) {
						start = false ;
						max_f = current_f ; 
						bus_id = i ;
						
					}
				    if(current_f > max_f) {
				    	max_f = current_f ; 
						bus_id = i ;
				    }
				
					
				}
		}
		}
		String result = bus_id + ";" + max_f ;
		return result ;
	}
	//calculateF
	private static float calculateF(int bus_id, Ride r, int assigned_bus_id) {
		// TODO Auto-generated method stub
		float TotalWastedDistance = 0 ;
		float TotalWaitingTime = 0 ;
		int sucessRide = 0 ;
		for(int i = 0 ; i < buses.size(); i++) {
			if(bus_id == i) {
				TotalWastedDistance += (buses.get(i).getWastedDistance() - buses.get(i).getFinal_distance_to_garage());
				TotalWaitingTime += buses.get(i).getWaitedTime() ;
				sucessRide +=  buses.get(i).getRideList().size();
				sucessRide += 1;
				float sd = getHaversineDistance( r.getsLat() , r.getsLat() ,buses.get(i).getCurrentlat() , buses.get(i).getCurrentlng());
				double timeTaken = (sd/60)*60 ;
				float arrival_time = (float) (buses.get(i).getBusTime() + timeTaken) ;
				if(buses.get(i).getRideList().size() != 0) {
					TotalWaitingTime += (r.getSt()- arrival_time);
				}
				
				float distanceToGarage = (float) getHaversineDistance( r.geteLat() , r.geteLng() , buses.get(i).getLat() , buses.get(i).getLng());
				TotalWastedDistance += (sd + distanceToGarage);
				
			}else {
				if(assigned_bus_id == i) {
					ArrayList<Integer> rides = buses.get(i).getRideList();
					float c_WastedDistance = 0;
					float c_TotalWaitingTime = 0 ;
					int last_ride_index = -1 ;
					sucessRide += (buses.get(i).getRideList().size() -1);
					float current_lat = buses.get(i).getLat();
					float current_lng = buses.get(i).getLng();
					for(int j = 0 ; j < rides.size(); j++) {
						Ride current_ride = requests.get(rides.get(j)-1) ;
						if(! (current_ride.get_id() ==  r.get_id())) {
							float sd = getHaversineDistance( current_ride.getsLat() , current_ride.getsLat() ,current_lat , current_lng);
							double timeTaken = (sd/60)*60 ;
							float arrival_time = (float) (buses.get(i).getBusTime() + timeTaken) ;
							if(j != 0) {
								c_TotalWaitingTime += (current_ride.getSt()- arrival_time);
							}
							c_WastedDistance += sd ;
							last_ride_index = j ;
						}
						
					}
					
					if(last_ride_index != -1) {
						Ride last_ride = requests.get(rides.get(last_ride_index)-1) ;
						float distanceToGarage = (float) getHaversineDistance( last_ride.geteLat() , last_ride.geteLng() , buses.get(i).getLat() , buses.get(i).getLng());
						TotalWastedDistance += (distanceToGarage + c_WastedDistance);
						TotalWaitingTime += c_TotalWaitingTime ;
						
					}
					
					
				}else {
					TotalWastedDistance += buses.get(i).getWastedDistance();
					TotalWaitingTime += buses.get(i).getWaitedTime() ;
					sucessRide += buses.get(i).getRideList().size();
					
				}
				
			}
			
				
				
		}
		float f = 60*sucessRide - 3*TotalWastedDistance - TotalWaitingTime ;
		return Float.parseFloat(df2.format(f));
	}

	public static Boolean checkTabuMemory (int ride_id , int bus_id) {
		String optimal_node = "" ;
		String [] current_rides = current_node.split(",");
		for(int m = 0 ; m < current_rides.length ; m ++) {
			if(m == ride_id) {
				optimal_node += bus_id  + ",";
			}else {
				optimal_node += current_rides[m] + ",";
			}
			
		}
		String s  = optimal_node.substring(0 , optimal_node.length()-1);
		if(tabuMemory.contains(s)) {
			return true ;
		}
		return false ;
	}
	//tabuSearch
	public static void tabuSearchAlogrithim() {
		previous_node = current_node;
		current_node = current_node.substring(0 , current_node.length()-1);
		float optimalF = -1 ;
		for(int k = 0 ; k < 100000 ; k ++) {
			tabuMemory.add(current_node);
			tabuMemoryIteration.add(1000);
			//optimal solution
			optimalF = -1 ;
			int pre_assignedBus = -1 ;
			int new_assignedBus = -1 ;
			int ride_id = -1 ;
			boolean start = true ;
			//nodeOfOptimal
			String optimal_node = "" ;
			String[] current_rides = current_node.split(",");
			//loop on each ride 
			for(int i = 0 ; i < current_rides.length ; i ++) {
				
				String result = checkMoveOnRide(optimalF , requests.get(i) , Integer.parseInt(current_rides[i]));
				String [] result_Array = result.split(";");
				float new_f = Float.parseFloat(result_Array[1]);
				if(start && (Integer.parseInt(result_Array[0]) != -1)) {
					new_assignedBus = Integer.parseInt(result_Array[0]) ;
					optimal_node = "" ;
					for(int m = 0 ; m < current_rides.length ; m ++) {
						if(m == i) {
							optimal_node += new_assignedBus  + ",";
						}else {
							optimal_node += current_rides[m] + ",";
						}
						
					}
					start = false ;
					optimalF = new_f;
					pre_assignedBus = Integer.parseInt(current_rides[i]);
					ride_id = i+1;
					
				}else {
					if( (new_f > optimalF ) && (Integer.parseInt(result_Array[0]) != -1)) {
						new_assignedBus = Integer.parseInt(result_Array[0]) ;
						optimal_node = "" ;
						for(int m = 0 ; m < current_rides.length ; m ++) {
							if(m == i) {
								optimal_node += new_assignedBus  + ",";
							}else {
								optimal_node += current_rides[m] + ",";
							}
							
						}
						optimalF = new_f;
						pre_assignedBus = Integer.parseInt(current_rides[i]);
						
						ride_id = i+1;
					}
				}
			}
			if(optimal_node.length() == 0) {
				String result = unAssignCurrentRide();
				String [] result_Array = result.split(";");
				if(result_Array.length > 1) {
				optimal_node = result_Array[0] ;
				optimalF = Float.parseFloat(result_Array[1]) ;
				ride_id = Integer.parseInt(result_Array[2]) + 1;
				pre_assignedBus =Integer.parseInt(result_Array[3]);
				new_assignedBus = -1 ;
				}
			}
			if(optimal_node.length() != 0) {
				previous_node = current_node ;
				current_node = optimal_node.substring(0 , optimal_node.length()-1);
				
				//checkOnBusesData
				UpdateOnBuses(pre_assignedBus , new_assignedBus , ride_id);
			
				
				
			}
			//System.out.println("lll "+ k+"  " + current_node + " f = " +  optimalF  + " pn " + previous_node);
			decrementTabuMemoryIteration();
			
			
		}
		
	}
	private static String unAssignCurrentRide() {
		// TODO Auto-generated method stub
		String[] current_rides = current_node.split(",");
		//loop on each ride 
		boolean start = true ;
		float optimal_f = 0 ;
		float new_f = 0 ;
		int ride_id = -1 ;
		int pre_assigned_bus_id = -1 ;
		for(int i = 0 ; i < current_rides.length ; i ++) {
			if(Integer.parseInt(current_rides[i]) != -1) {
				
				if(start && (!checkTabuMemory(i , -1)) ) {
					start = false ;
					pre_assigned_bus_id = Integer.parseInt(current_rides[i]) ;
					optimal_f =  calculateF(-1 , requests.get(i), Integer.parseInt(current_rides[i]));
					//unAssign 
					ride_id = i ;
				}else {
					
				    new_f = calculateF(-1 , requests.get(i), Integer.parseInt(current_rides[i]));
					if((new_f > optimal_f ) && (!checkTabuMemory(i , -1))) {
						optimal_f = new_f ;
						//unAssign this ride
						ride_id = i ;
						pre_assigned_bus_id = Integer.parseInt(current_rides[i]) ;
					}
					
				}
			}
		}
		//create new_node
		String optimal_node = "" ;
		if(ride_id  != -1) {
			for(int m = 0 ; m < current_rides.length ; m ++) {
				if(m == ride_id) {
					optimal_node += -1  + ",";
				}else {
					optimal_node += current_rides[m] + ",";
				}
				
			}
			return optimal_node + ";" + optimal_f +";" + ride_id + ";" + pre_assigned_bus_id;
		}
		
		return "-1";
		
	}
	//getHaversineDistance
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

	        double r =  6371;
	       
	        // calculate the result
	        return Float.parseFloat(df2.format((c * r)));
	    
	    

	}
	

	public static void main(String[] args) {
		 try {
			 //search For file
		      File myObj = new File("D:/p/books/projects/SWVL task/inputs/s1.in");
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		    	String data = myReader.nextLine();
		    	String[] dataList = data.split(" ");
		    	
		    	if(num_line == 0) {
		    		num_requests = Integer.parseInt(dataList[0]) ;
		    		num_buses = Integer.parseInt(dataList[1]);
		    		num_line+=1 ;
		    		
		    	}else {
		    		
		    		if(num_requests > 0) {
			    		//create requests 
			    		float sLat = Float.parseFloat(dataList[0]);
			    		float sLng = Float.parseFloat(dataList[1]);
			    		int st = Integer.parseInt(dataList[2]);
			    		float eLat = Float.parseFloat(dataList[3]);
			    		float eLng = Float.parseFloat(dataList[4]) ;
			    		int et = Integer.parseInt(dataList[5]);
			    		
			    		Ride new_req = new Ride(sLat , sLng , eLat , eLng , st ,et );
			    		requests.add(new_req);
			    		int ride_id = new_req.get_id();
			    		current_node+= -1 + "," ;
			    		num_requests -= 1 ;
			    		
			    	}else {
			    		
			    		 if((num_buses > 0)) {
					        	//create buses 
					        	float lat = Float.parseFloat(dataList[0]);
					    		float lng = Float.parseFloat(dataList[1]) ;
					        	Bus new_bus = new Bus(lat , lng);
					    		buses.add(new_bus);
					    		current_bus_options += new_bus.get_id() + ";" ;
					    		num_buses -= 1 ;
					        	
					        }
			    	}
			       
		    	}
		    	
		        
		        
		      }
		      myReader.close();
		      
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		 tabuSearchAlogrithim();
		 float Wasted_D = 0 ;
		 float Waited_T = 0 ;
		 int S_A = 0 ;
		 String result = "" ; 
		 for(int i = 0 ; i < buses.size() ; i++) {
			 ArrayList <Integer>  rides = buses.get(i).getRideList();
			 Waited_T += buses.get(i).getWaitedTime() ;
			 Wasted_D += buses.get(i).getWastedDistance()  ;
			 S_A += rides.size() ;
			 result += rides.size() + " " ;
			 for(int j = 0 ; j < rides.size() ; j++) {
				 int busRide = rides.get(j);
				 result += busRide +" " ;
			 }
			 result += "\n" ;
			
	      }
		 float TotalWastedDistance = Float.parseFloat(df2.format(3*Wasted_D));
		 float TotalWaitedTime = Float.parseFloat(df2.format(Waited_T));
//		 System.out.println(" f = " + (60*S_A -(TotalWastedDistance) - TotalWaitedTime));
//		 System.out.println(" Wasted_D = " + (Wasted_D));
//		 System.out.println(" Waited_T = " + (Waited_T));
//		 System.out.println(" S_A = " + (S_A));
		 float f = (60*S_A -(TotalWastedDistance) - TotalWaitedTime) ;
	     System.out.println(f);
		 System.out.println(result);
	}
}
