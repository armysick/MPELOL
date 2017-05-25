import java.util.ArrayList;
import java.util.Collections;

public class Main {
	
	private static ArrayList<Slot> slots = new ArrayList<Slot>();
	private static ArrayList<Schedule> population = new ArrayList<Schedule>();
	private static ArrayList<Matchup> matchups = new ArrayList<Matchup>();
	private static ArrayList<Team> teamsA = new ArrayList<Team>();
    private static ArrayList<Team> teamsB = new ArrayList<Team>();
	
	public static void main (String[] args){

        StartGroupA();
      //  StartGroupB();
        Schedule initial_Split = population.get(0);
        System.out.println("=========================================================================================");
        System.out.println("\t\t\t\t\t\tInitial Schedule");
        PrintResult(initial_Split);
        System.out.println("=========================================================================================");
        System.out.println("=========================================================================================");
        /* testing PEAST */
		PEAST peastAlg = new PEAST();
        double current_temperature = 1/Math.log(Math.pow(0.75,-1));
        Schedule newSchedule = peastAlg.GHCM(initial_Split,20,0,current_temperature);
        /* END of test */

        System.out.println("=========================================================================================");
        System.out.println("\t\t\t\t\t\tGHCM Schedule");
        PrintResult(newSchedule);
        System.out.println("=========================================================================================");
        System.out.println("=========================================================================================");
	}
	
	//Consult GroupANotes.txt
	public static void StartGroupA(){
		FillUpTeamsArrayA();
		FillUpMatchupsA();
		//FillUpSlotsA();
        FirstPartFillUpSlotsA();
		FillUpPopulationA();
        System.out.println("Size of current ["+ 1 +"] population sample: " + population.size());
		AssignRandomMatchupsToSlots();
	}
    public static void StartGroupB(){
        FillUpTeamsArrayB();
    /*    FillUpMatchupsA();
        FillUpSlotsA();
        AssignRandomMatchupsToSlots();*/
    }
	
	public static void FillUpTeamsArrayA(){
        teamsA.add(new Team("G2ESPORTS", 50));   // Team 0 - teams.get(0)
        teamsA.add(new Team("MISFITS", 30));     // Team 1 - teams.get(1)
        teamsA.add(new Team("ROCCAT", 20));      // Team 2 - teams.get(2)
        teamsA.add(new Team("FNATIC", 50));      // Team 3 - teams.get(3)
        teamsA.add(new Team("GIANTS", 10));      // Team 4 - teams.get(4)
	}

    public static void FillUpTeamsArrayB(){
        teamsB.add(new Team("UNICORNS OF LOVE", 50));
        teamsB.add(new Team("H2K-GAMING", 40));
        teamsB.add(new Team("SPLYCE", 30));
        teamsB.add(new Team("VITALIY", 30));
        teamsB.add(new Team("ORIGEN", 30));
    }

	public static void FillUpMatchupsA(){
		matchups.add(new Matchup(teamsA.get(0), teamsA.get(1)));
		matchups.add(new Matchup(teamsA.get(0), teamsA.get(2)));
		matchups.add(new Matchup(teamsA.get(0), teamsA.get(3)));
		matchups.add(new Matchup(teamsA.get(0), teamsA.get(4)));
		matchups.add(new Matchup(teamsA.get(1), teamsA.get(2)));
		matchups.add(new Matchup(teamsA.get(1), teamsA.get(3)));
		matchups.add(new Matchup(teamsA.get(1), teamsA.get(4)));
		matchups.add(new Matchup(teamsA.get(2), teamsA.get(3)));
		matchups.add(new Matchup(teamsA.get(2), teamsA.get(4)));
		matchups.add(new Matchup(teamsA.get(3), teamsA.get(4)));
	}

    public static void FirstPartFillUpSlotsA(){
        slots.add(new Slot(false, 1, 1));
        slots.add(new Slot(true, 1, 1));
        slots.add(new Slot(false, 2, 1));
        slots.add(new Slot(true, 2, 1));
        slots.add(new Slot(false, 3, 1));
        slots.add(new Slot(true, 3, 1));
        slots.add(new Slot(false, 4, 1));
        slots.add(new Slot(true, 4, 1));
        slots.add(new Slot(false, 1, 2));
        slots.add(new Slot(true, 1, 2));
        slots.add(new Slot(false, 2, 2));
        slots.add(new Slot(true, 2, 2));
        slots.add(new Slot(false, 3, 2));
        slots.add(new Slot(true, 3, 2));
        slots.add(new Slot(false, 4, 2));
        slots.add(new Slot(true, 4, 2));
      /*  slots.add(new Slot(false, 1, 3));
        slots.add(new Slot(true, 1, 3));
        slots.add(new Slot(false, 2, 3));
        slots.add(new Slot(true, 2, 3));
        slots.add(new Slot(false, 3, 3));
        slots.add(new Slot(true, 3, 3));
        slots.add(new Slot(false, 4, 3));
        slots.add(new Slot(true, 4, 3));*/

    }

	public static void FillUpSlotsA(){
		//Slot(primetime, day, week)
		slots.add(new Slot(true, 1, 1)); 
		slots.add(new Slot(false, 2, 1)); 
		slots.add(new Slot(false, 3, 1));
		slots.add(new Slot(true, 1, 2));
		slots.add(new Slot(true, 2, 2));
		slots.add(new Slot(true, 3, 2));
		slots.add(new Slot(true, 4, 2));
		slots.add(new Slot(true, 1, 3));
		slots.add(new Slot(true, 2, 3));
		slots.add(new Slot(true, 3, 3));
	}

	public static void FillUpPopulationA(){
        int daysPerWeek = 4;
        int weeks = 2;
        int slotsPerDay = 2;
        int sizeOfPopulation = daysPerWeek * weeks * slotsPerDay;
		for(int i = 0; i < sizeOfPopulation ; i++)
			population.add(new Schedule(slots));
	}
	
	public static void AssignRandomMatchupsToSlots(){
        int daysPerWeek = 4;
        int weeks = 2;
        int slotsPerDay = 2;
        int sizeOfPopulation = daysPerWeek * weeks * slotsPerDay;
		for(int i = 0; i < sizeOfPopulation ; i++)
			population.get(i).AssignRandomMatchups(matchups);
	}

    public static void PrintResult(Schedule split) {
        int currentWeek = 1;
        System.out.println("\t\t\t\t\t  Week "+ 1 +" Game Slots");
        for(Slot slot : split.getSlots()) {
            if(currentWeek != slot.getWeek()) {
                System.out.println("\t\t\t\t\t  Week "+ slot.getWeek() +" Game Slots");
                System.out.println("-------------------------------------------------------------------");
                currentWeek++;
            }

            System.out.println(slot.PrintSlot());
        }
    }
}
