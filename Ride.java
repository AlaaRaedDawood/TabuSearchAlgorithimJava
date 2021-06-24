package hackerank;

public class Ride {
	private static int request_id = 1 ; 
	private float sLat ;
	private float sLng ;
	private float eLat ;
	private float eLng ;
	private int st;
	private int et;
	private int id ; 
	private int assigned ; 
	public Ride(float sLat , float sLng , float eLat ,float eLng , int st,  int et) {
		this.sLat = sLat ;
		this.sLng = sLng;
		this.eLat = eLat;
		this.eLng = eLng;
		this.st = st;
		this.et = et;
		this.id = request_id ;
		this.assigned = -1 ;
		request_id += 1 ;
		
	}
	
	public int get_id() {
		return id ;
	}
	public int getEt() {
		return et;
	}
	public void setEt(int et) {
		this.et = et;
	}
	public float getsLat() {
		return sLat;
	}
	public void setsLat(float sLat) {
		this.sLat = sLat;
	}
	public float getsLng() {
		return sLng;
	}
	public void setsLng(float sLng) {
		this.sLng = sLng;
	}
	public float geteLat() {
		return eLat;
	}
	public void seteLat(float eLat) {
		this.eLat = eLat;
	}
	public float geteLng() {
		return eLng;
	}
	public void seteLng(float eLng) {
		this.eLng = eLng;
	}
	public int getSt() {
		return st;
	}
	public void setSt(int st) {
		this.st = st;
	}
	
	

}
