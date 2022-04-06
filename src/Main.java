import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Main {
	
	private static LinkedList<Task> tasks = new LinkedList<>(); //stores all the tasks in ascending order regarding start times
	private static LinkedList<Task> runningTasks = new LinkedList<>(); //stores a backup pointer of all task because we delete them from tasks when giving to the scheduler; used only for determining waiting times
	static boolean schedulersEmpty = true; //signals if the schedulers emptied out
	
	private static Global scheduler = new Global();
	
	public static void main(String[] args) {
		
		//reading input
		//-------------
		
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
		
		String currentLine;
		try {
			
			currentLine = sysIn.readLine();
			while (!(currentLine==null) && !currentLine.equals("")) {
				String[] lineContent = currentLine.split(",");
				Task readTask = new Task(lineContent[0].charAt(0), lineContent[1].equals("1"), Integer.parseInt(lineContent[2]), Integer.parseInt(lineContent[3]));
				
				//inserting read task to tasks
				if (tasks.size() == 0) {tasks.add(readTask);}
				else {
					int i=0;
					while(tasks.size() > i && tasks.get(i).startTime <= readTask.startTime) i++;
					tasks.add(i, readTask);
				}
				
				currentLine = sysIn.readLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//running simulation
		//------------------
		
		//special case: no tasks
		if(tasks.size()==0) {System.out.println("\n\n"); return;}
		
		for(int now = 0; tasks.size()>0 || !schedulersEmpty; now++) {
			
			//adding incoming tasks
			for(int i=0; i<tasks.size(); i++) {
				if(tasks.get(i).startTime == now) {
					scheduler.add(tasks.get(i));
					runningTasks.add(tasks.get(i));
					tasks.remove(i);
					i--; //we just removed an item so the i-th is the next one
				}
			}
			
			//ticking
			scheduler.tick();
		}
		
		//writing output
		//--------------
		
		System.out.println(scheduler.history);
		
		System.out.print(runningTasks.get(0).name + ":" + runningTasks.get(0).getWaitingTime());
		for(int i = 1; i< runningTasks.size(); i++) {
			System.out.print("," + runningTasks.get(i).name + ":" + runningTasks.get(i).getWaitingTime());
		}
		
	}
	
}
