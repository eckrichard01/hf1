import java.util.LinkedList;

/**
 * An SRTF (shortest remaining time first) scheduler designed for high priority tasks
 */
public class SRTF {
	
	private final Global global;

	private static final int timeSlice = 2; //time in ticks while a task is allowed to run
	private int runningFor = 0; //the number of timeslices the active task has been running for

	private LinkedList<Task> waitingQueue = new LinkedList<>(); //inactive tasks
	private Task activeTask = null; //active task; null if there are none
	
	private boolean needSwap = true; //stores if the active task should be changed
	
	/**
	 * constructor
	 * @param global the multilevel scheduler to which this one belongs
	 */
	public SRTF(Global global) {
		this.global = global;
	}
	
	public void add(Task t) {
		waitingQueue.add(t);
		
		if(activeTask == null || t.remainingTime < activeTask.remainingTime) needSwap = true;
	}
	
	/**
	 * signals a processor tick
	 */
	public void tick() {
		if (isEmpty()) return;

		//swapping if necessary
		if (needSwap) {
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

			Task temp = activeTask;
			activeTask = waitingQueue.get(minIndex);
			waitingQueue.remove(minIndex);
			if(temp != null) waitingQueue.add(temp);
			
			global.changeRunning(activeTask);

			needSwap = false;
		}
		if(waitingQueue.size() > 0 && (runningFor == timeSlice || activeTask == null)) { //we swap if we can and if we need to; we need to when the running period is over or the current task is not running anymore

			if (activeTask != null) waitingQueue.addLast(activeTask);
			activeTask = waitingQueue.removeFirst();

			global.changeRunning(activeTask);

			runningFor = 0;
		}

		if (runningFor == timeSlice) runningFor = 0;
		
		//registering time flowing
		for(Task t : waitingQueue) t.waitOne();
		activeTask.runOne();
		if(activeTask.remainingTime == 0) {
			needSwap = true;
			activeTask = null;
		}
		runningFor++;
	}
	
	public boolean isEmpty() {
		return (activeTask==null && waitingQueue.size()==0);
	}
}
