import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class PEAST {

	private Matchup in_hand_matchup = null;
	private int empty_slot = -1;

    public PEAST(){  }

    public Schedule run(int iteration_limit, int cloning_interval, int shuffling_interval, ArrayList<Schedule> population){
        int update_interval = 0; // ADAGEN update interval
        int population_size = population.size();
        Schedule best_solution = new Schedule();
        int round = 1;
        double current_temperature = 1/Math.log(Math.pow(0.75,-1)); // T0, initial temperature
        int maximum_sequence_length = iteration_limit;  // maximum sequence length (m) is equal to the maximum number of iterations (t)
        while(round <= iteration_limit){
            int index = 1;
            while(index++ <= population_size){
               //TODO Apply GHCM to schedule Sindex to get a new schedule
                best_solution = GHCM(population.get(index), 10, round, current_temperature);
                if (population.get(index).getScheduleValue() < best_solution.getScheduleValue()){
                    best_solution = population.get(index);
                }
            }
            current_temperature = getTemperature(current_temperature,maximum_sequence_length);
            //TODO Update simulated annealing framework

            if (round % update_interval == 0) {
                //TODO Update ADAGEN Framework
            }
            if (round % shuffling_interval == 0) {
                //TODO Apply shuffling operators
            }
            if (round % cloning_interval == 0) {
                //TODO Replace the worst schedule with the best one
            }
            round++;
        }
        return best_solution;
    }

    public Schedule GHCM(Schedule split, int maximum_sequence_length, int current_round, double current_temperature){
        int k = current_round/2;
        Matchup object_to_move = randomObject(split); // Set objectToMove = RandomObject(S)
        int target_cell_index = 0;
        int index = 0;
        double last_increment = Double.POSITIVE_INFINITY;
        double costDiff = 0.0;
        Schedule copy = new Schedule(split);
        Schedule last_optimal = new Schedule(split);
        double optimal_cost = 0.0;
        Matchup save_hand = new Matchup();

        System.out.println("Initial Schedule Value: " + split.getScheduleValue() +"\n");
        Random r = new Random();
        int firstCell = r.nextInt(split.getSlots().size()-0) + 0;
        while(index < maximum_sequence_length){
            double originalIterationCost = copy.getScheduleValue(); // currentCost of the iteration is the full cost(value) of a split
            System.out.println("Cost of iteration " + index + " is: " + originalIterationCost);

            if (index > 0) {

                // Inner annealing #1
                if (Math.random() < Math.exp(-1 / current_temperature))
                {
                    System.out.println("[object to move] --> Used Annealing <--");
                    if (index == 1) {
                        in_hand_matchup = copy.getSlots().get(target_cell_index).getMatch_assigned();
                        object_to_move = copy.getSlots().get(firstCell).getMatch_assigned();
                        copy.getSlots().get(firstCell).UnAssignMatchup();
                    } else {
                        Matchup temp = copy.getSlots().get(target_cell_index).getMatch_assigned();
                        object_to_move = in_hand_matchup;
                        in_hand_matchup = temp;
                    }
                }else
                {
                    System.out.println("[object to move] --> Used LeastFit <--");
                    if (index == 1) {
                        in_hand_matchup = copy.getSlots().get(target_cell_index).getMatch_assigned();
                        int matchup_picked_to_move = leastFitObject(copy, target_cell_index); // Set objectToMove = LeastFitObject(S,targetCell)
                        object_to_move =  copy.getSlots().get(matchup_picked_to_move).getMatch_assigned();
                        copy.getSlots().get(matchup_picked_to_move).UnAssignMatchup();
                    } else {
                        Matchup temp = copy.getSlots().get(target_cell_index).getMatch_assigned();
                        object_to_move = in_hand_matchup;
                        in_hand_matchup = temp;
                    }
                }

                copy.getSlots().get(target_cell_index).AssignMatchup(object_to_move);
                if(in_hand_matchup != null)
                    System.out.println("Hand : " + in_hand_matchup.PrintMatchup());
                if(object_to_move != null)
                    System.out.println("Object To Move : " + object_to_move.PrintMatchup());
                System.out.println("Cell that's going to receive this New Match : " + target_cell_index);
            }

            // Inner annealing #2
            if(Math.random() < Math.exp(-1 / current_temperature))
            {
                System.out.println("[Cell to move] --> Used Annealing <--");
                target_cell_index = randomCell(copy); // Set targetCell = RandomCell(S)
                System.out.println("!! Cell Slot picked For next Iteration :" + target_cell_index);
            }
            else
            {
                System.out.println("[Cell to move] --> FittestCell <--");
                //target_cell_index = fittestCell(copy, object_to_move); // Set targetCell = FittestCell(S,objectToMove)
                target_cell_index = fittestCell(copy); // Set targetCell = FittestCell(S,objectToMove)
                System.out.println("!! Cell Slot picked For next Iteration :" + target_cell_index);
            }

            double current_cost = copy.getScheduleValue();

            if(current_cost > optimal_cost)
            {
                optimal_cost = current_cost;
                System.out.println("Optimal value Cost:" + optimal_cost);
                if(in_hand_matchup != null)
                    save_hand = in_hand_matchup;
                last_optimal = new Schedule(copy);
            }
         /*   if (current_cost > originalIterationCost)
            {
                costDiff = current_cost - originalIterationCost;
                if (costDiff > last_increment){
                    index = Integer.MAX_VALUE;
                    System.out.println("MAX_VALUE");
                    break; // This is probably wrong
                }
                else{
                    last_increment = costDiff;
                }
            }*/
            index++;

            for (int i = 0; i < copy.getSlots().size();i++)
            {
                if(i != 0)
                {
                    if (copy.getSlots().get(i).getWeek() != copy.getSlots().get(i-1).getWeek())
                    {
                        System.out.println("----------  ----------  ----------  ----------  ----------  ----------  ----------  ----------  ----------");
                    }
                }
                System.out.println(copy.getSlots().get(i).PrintSlot());
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
        Schedule returnValue = new Schedule();

        // Outer annealing
        if( index == Integer.MAX_VALUE) {
            if (Math.random() < Math.exp(-costDiff / current_temperature)) {
                returnValue = new Schedule(copy);
            }
        }
        else{
            returnValue = new Schedule(last_optimal); // Roll S back to the optimal point in the move sequence.
        }

        // TODO Se isto estiver aqui vai poderá corromper o valor da solução pq é adicionada mais tarde, será uma corrupção minima? Ou demasiado grande?
        for (int i = 0; i < returnValue.getSlots().size();i++)
        {
            if (returnValue.getSlots().get(i).getMatch_assigned() == null)
            {
                if(save_hand == null)
                    System.out.println("Hand : "+ save_hand.PrintMatchup());
                returnValue.getSlots().get(i).AssignMatchup(save_hand);
                break;
            }
        }

    /*    System.out.println("Split       " + split);
        System.out.println("ReturnValue " + returnValue);
        System.out.println("Split       " + split.getSlots());
        System.out.println("ReturnValue " + returnValue.getSlots());
        System.out.println("Split        Match " + split.getSlots().get(0).getMatch_assigned());
        System.out.println("ReturnValue  Match " + returnValue.getSlots().get(0).getMatch_assigned());*/

        System.out.println();
        System.out.println("Initial Schedule Value: " + split.getScheduleValue());
        System.out.println("Optimal Cost: " + optimal_cost);
        System.out.println("Cost of returnValue: " +  returnValue.getScheduleValue());
       // SaveToFile(current_round,everyCost,split, returnValue);

        PrintResult(returnValue,1);
        return returnValue;
    }

    // TEMP discordo
    public int leastFitObject(Schedule split, int target_cell_index){
        double least_fit_value = split.getScheduleValue();
        Slot target_cell = split.getSlots().get(target_cell_index);
        Matchup target_cell_matchup = target_cell.getMatch_assigned();
        //Matchup least_fit = target_cell.getMatch_assigned();
        int least_fit_index = 0;
        for(int i = 0; i < split.getSlots().size() ;i++){
            Schedule temp = new Schedule(split);

            if(i == target_cell_index)
                continue;

            if(temp.getSlots().get(i).getMatch_assigned() == null)
            	continue;

            Matchup candidate = new Matchup(temp.getSlots().get(i).getMatch_assigned());
            temp.getSlots().get(i).AssignMatchup(target_cell_matchup);
            temp.getSlots().get(target_cell_index).AssignMatchup(candidate);

            double schedule_cost = temp.getScheduleValue();

            if(schedule_cost > least_fit_value) {
                least_fit_value = schedule_cost;
                //least_fit = candidate;
                least_fit_index = i;
            }
        }

        return least_fit_index;
    }

    public int fittestCell(Schedule split ){
        double fittest_cell_value = split.getSlots().get(0).getValue();
        int fittest_cell_index = 0;
        for(int i = 0; i < split.getSlots().size() ;i++){
            if(fittest_cell_value > split.getSlots().get(i).getValue()){
                fittest_cell_value = split.getSlots().get(i).getValue();
                fittest_cell_index = i;
            }
        }

        return fittest_cell_index;
    }

    // TEMP discordo
    public int fittestCell(Schedule split, Matchup matchup_to_move){
        double fittest_cell_value = split.getScheduleValue();
        int original_cell_index = 0;
        for (int i = 0; i < split.getSlots().size(); i++){
            if (split.getSlots().get(i).getMatch_assigned().equals(matchup_to_move))
            {
                original_cell_index = i;
                break;
            }
        }

        int fittest_cell_index = original_cell_index;

        for(int i = 0; i < split.getSlots().size() ;i++){
            Schedule copy = new Schedule(split);

            if(i == original_cell_index)
                continue;

            if(copy.getSlots().get(i).getMatch_assigned() == null)
                continue;

            Slot candidate = new Slot(copy.getSlots().get(i)); // TEMP

            copy.getSlots().get(original_cell_index).AssignMatchup(candidate.getMatch_assigned()); // TEMP
            candidate.AssignMatchup(matchup_to_move);

            double schedule_cost = copy.getScheduleValue();

            if(schedule_cost < fittest_cell_value) {
                fittest_cell_value = schedule_cost;
                fittest_cell_index = i;
            }
        }

        return fittest_cell_index;
    }

    /**
     * Support function for getting a random Matchup from a Schedule
     * @param schedule
     * @return Matchup currently selected from the split, it will become the matchup
     */
    private Matchup randomObject(Schedule schedule)
    {
        boolean checkForNull = false;
        Matchup matchupToReturn = new Matchup();
        while (!checkForNull){
            int index = new Random().nextInt(schedule.getSlots().size());
            matchupToReturn = schedule.getSlots().get(index).getMatch_assigned();
            if (matchupToReturn != null){
                checkForNull = true;
            }
        }

        return matchupToReturn;
    }

    private Matchup randomObject(Schedule schedule, int target_cell_index)
    {
        //TODO target_cell is used to assert constraints for the object movement.
        return randomObject(schedule);
    }

    private int randomCell(Schedule schedule)
    {
        return new Random().nextInt(schedule.getSlots().size());
    }

    public double getTemperature(double T0, int m){
        double p = 0.0015;
        return Math.pow((-1/(T0 *Math.log(p))), (1/m));
    }

    public void SaveToFile(int index, ArrayList<Double> everyCost, Schedule originalSplit, Schedule returnValue) {
        try{
            BufferedWriter output = null;
            File file = new File("C:\\Users\\Utilizador\\Documents\\Faculdade\\2016-2017\\2Semestre\\MPES\\Projeto\\SavedRuns\\" + "iteration" + index + ".txt");
            output = new BufferedWriter(new FileWriter(file));
            output.write("===================================================================");
            output.write("First Schedule");
            output = PrintSplit(originalSplit,output);
            output.write("===================================================================");
            output.write("===================================================================");
            for (int i = 0; i < everyCost.size(); i++) {
                output.write("Cost of " + i + " is: " + everyCost.get(i));
                output.write("Cost of returnValue:" + returnValue.getScheduleValue());
            }
            output.write("===================================================================");
            output.write("GHCM Schedule");
            output = PrintSplit(returnValue,output);
            output.write("===================================================================");
            output.write("===================================================================");
        }catch (IOException e) {
        }
    }

    public static void PrintResult(Schedule split, int round_robin) {
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

    public BufferedWriter PrintSplit(Schedule split, BufferedWriter output){
        int currentWeek = 1;
        try{
            for(Slot slot : split.getSlots()) {
                if(currentWeek != slot.getWeek()) {
                    output.write("-------------------------------------------------------------------");
                    currentWeek++;
                }
                output.write("\t\tWeek "+ slot.getWeek() +" Game Slots");
                output.write(slot.PrintSlot());
            }
        }
        catch (IOException e){

        }
        return output;
    }
}
