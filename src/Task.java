/**
 * A task to be scheduled in the model
 */
public class Task {
	
	public final char name;
	
	public final boolean hihgPriority;
	
	public final int startTime;
	public int remainingTime;
	private int waitingTime = 0;
	
	public Task(char name, boolean isKernel, int start, int duration) {
		this.name = name;
		this.hihgPriority = isKernel;
		startTime = start;
		remainingTime = duration;
	}
	
	/**
	 * makes the task wait for one tick
	 */
	public void waitOne() {waitingTime++;}
	
	public int getWaitingTime() {
		return waitingTime;
	}
	
	/**
	 * makes the task run for one tick
	 */
	public void runOne() {remainingTime--;}
}
