import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A round-robin scheduler designed for low priority tasks - so it can be stopped when a higher priority task comes in and restarted later.
 */
public class RR {
	
	private final Global global;
	
	private static final int timeSlice = 2; //time in ticks while a task is allowed to run
	private int runningFor = 0; //the number of timeslices the active task has been running for
	
	private boolean enabled = true;

	private LinkedList<Task> waitingQueue = new LinkedList<>(); //inactive tasks
	private Task activeTask = null; //the active task; null if there are none

	/**
	 * Constructor
	 * @param global the multilevel scheduler to which this one belongs
	 */
	public RR(Global global) {
		this.global = global;
	}
	
	/**
	 * Get back the right of scheduling tasks
	 */
	public void start() {
		enabled = true;
	}
	
	/**
	 * lose the right of scheduling tasks (e.g. because a higher priority task came in)
	 */
	public void stop() {
		enabled = false;
		
		//forcing the active task to sleep
		runningFor = 0;
		if (activeTask != null) waitingQueue.addLast(activeTask);
		activeTask = null;
	}
	
	public void add(Task t) {
		waitingQueue.addLast(t);
	}
	
	/**
	 * signals a processor tick
	 */
	public void tick() {
		if(!enabled) { //if not enabled, every task waits
			if (activeTask != null) activeTask.waitOne();
			for(Task t : waitingQueue) t.waitOne();
			return;
		}
		
		//swapping
		if(waitingQueue.size() > 0 && (runningFor == timeSlice || activeTask == null)) { //we swap if we can and if we need to; we need to when the running period is over or the current task is not running anymore
			int minIndex = -1; int minIndexadd= -1; int minValue = Integer.MAX_VALUE; int start = Integer.MAX_VALUE; int name = Integer.MAX_VALUE;
			for(int i=0; i<waitingQueue.size(); i++) {
				if(waitingQueue.get(i).getWaitingTime() == 0){
					start = waitingQueue.get(i).startTime; minIndexadd = i;
				}
				if(waitingQueue.get(i).startTime == start && start != 0 && waitingQueue.get(i).getWaitingTime() == waitingQueue.get(minIndexadd).getWaitingTime() && waitingQueue.get(i).getWaitingTime() == 0){
					if(waitingQueue.get(i).name < name){
						name = waitingQueue.get(i).name; minIndex = i;
						if (waitingQueue.get(i).remainingTime < minValue){
							minValue = waitingQueue.get(i).remainingTime;
						}
					}
				}
				else if (waitingQueue.get(i).remainingTime < minValue) {minValue = waitingQueue.get(i).remainingTime; minIndex = i;}
			}
			if(activeTask != null && activeTask.remainingTime < minValue){

			}
			else{
				Task temp = activeTask;
				activeTask = waitingQueue.get(minIndex);
				waitingQueue.remove(minIndex);
				if(temp != null) waitingQueue.add(temp);
			}

			global.changeRunning(activeTask);

			runningFor = 0;
		}

		if (runningFor == timeSlice) runningFor = 0; //resetting runningFor even without swapping so that we will detect the next occasion when the time slice is over

		//registering time flow
		for(Task t : waitingQueue) t.waitOne();
		if(activeTask!=null) {
			activeTask.runOne();
			if(activeTask.remainingTime == 0) activeTask = null;
		}
		runningFor++;
	}
	
	public boolean isEmpty() {
		return activeTask==null && waitingQueue.size()==0;
	}
	
}
