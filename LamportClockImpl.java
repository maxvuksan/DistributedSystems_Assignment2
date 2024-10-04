package lamport;

public class LamportClockImpl implements LamportClock {

	private int time;

	
	@Override
	public void update(int recievedTime) {
		time = Math.max(time, recievedTime) + 1;
	}

	@Override
	public int increment() {
		time++;
		return time;
	}

	@Override
	public int getTime() {
		return time;
	}
}