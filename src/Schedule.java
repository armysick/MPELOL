import java.util.ArrayList;
import java.util.Collections;

public class Schedule {

	private ArrayList<Slot> slots = new ArrayList<Slot>();
	
	public Schedule() {

	}
	
	public Schedule(ArrayList<Slot> slots){
		this.slots = (ArrayList<Slot>) slots.clone();
	}

    public Schedule (Schedule s){
        for (int i = 0; i < s.getSlots().size(); i++){
            this.slots.add(new Slot(s.getSlots().get(i)));
        }
    }

	public void AssignRandomMatchups(ArrayList<Matchup> matchups) {
		Collections.shuffle(matchups);

		int index = 0;
		for (Matchup m : matchups) {
			this.slots.get(index).AssignMatchup(m);
			index++;
		}
	}

	public double getScheduleValue(){
		double scheduleValue = 0.0;
		for (int i = 0; i< slots.size();i++){
			scheduleValue += slots.get(i).getValue();
		}
		System.out.println("VALUE : " + scheduleValue);
		return 1/scheduleValue;
	}

	public ArrayList<Slot> getSlots(){
		return slots;
	}

    public void setSlots(ArrayList<Slot> slots)
	{
			this.slots = (ArrayList<Slot>) slots.clone();
    }
    public void PrintSchedule() {
        int currentWeek = 1;
        if ((slots.get(0).getWeek() >= 8) && (slots.get(0).getWeek() <= 10))
            currentWeek = 8;
        else if ((slots.get(0).getWeek() >= 4) && (slots.get(0).getWeek() <= 7) )
            currentWeek = 4;
        else if ((slots.get(0).getWeek() >= 1) && (slots.get(0).getWeek() <= 3) )
            currentWeek = 1;

        System.out.println("\t\t\t\t\t  Week "+ currentWeek +" Game Slots");
        for(Slot slot : slots) {
            if(currentWeek != slot.getWeek()) {
               System.out.println("\t\t\t\t\t  Week "+ slot.getWeek() +" Game Slots");
              System.out.println("-------------------------------------------------------------------");
                currentWeek++;
          }
            System.out.println(slot.PrintSlot());
        }
	}
}
