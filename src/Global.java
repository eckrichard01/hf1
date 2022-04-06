/**
 * the global scheduler
 * It will distribute the incoming tasks between the two schedulers (RR & SRTF) and select which one may determine the active task
 */
public class Global {
	
	private SRTF srtf = new SRTF(this); //high priority scheduler
	private RR rr = new RR(this); //low priority scheduler
	
	String history = ""; //a string with the names of task that have taken control so far - I will use this only for the output
	
	public void add(Task t) {
		Main.schedulersEmpty=false;
		
		if (t.hihgPriority) {
			srtf.add(t);
		} else {
			rr.add(t);
		}
	}
	
	public void tick() {
		if(srtf.isEmpty() && rr.isEmpty()) {Main.schedulersEmpty = true; return;}
		else if(!srtf.isEmpty()) rr.stop(); //if there is a high priority task, we will choose that and not let the low priority scheduler work
	
		srtf.tick();
		rr.tick();
		if (srtf.isEmpty()) rr.start();
	}
	
	/**
	 * A task is taking control
	 * @param t the task to be run
	 */
	public void changeRunning(Task t) {
		if(history.length() > 0 && history.charAt(history.length() - 1) == t.name){
			history += "";
		}
		else {
			history += t.name;
		}
	}
	
}
