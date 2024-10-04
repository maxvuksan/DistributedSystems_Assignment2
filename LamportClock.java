package lamport;


public interface LamportClock {
	
	/*
	 * Update the internal Lamport Clock for events other than message receive
	 * 
	 * @return the current Lamport Clock value
	 */
	public int increment();
	
	/*
	 * Get the current Lamport Time
	 * 
	 * @return the current Lamport clock value 
	 */
	public int getTime();

	// sets the time to max(time, recievedTime) + 1
	public void update(int recievedTime);
}