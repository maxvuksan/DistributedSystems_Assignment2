package util;


public class LamportClock {

	private int time = 0;
	
	/*
	 * Update the internal Lamport Clock for events other than message receive
	 * 
	 * @return the current Lamport Clock value
	 */
	public int increment(){
		time++;
		return time;
	}
	
	/*
	 * Get the current Lamport Time
	 * 
	 * @return the current Lamport clock value 
	 */
	public int getTime(){
		return time;
	}

	// sets the time to max(time, recievedTime) + 1
	public void update(int recievedTime){
		time = Math.max(time, recievedTime) + 1;
	}
}