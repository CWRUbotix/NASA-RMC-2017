package unit_tests;

import java.util.InputMismatchException;
import java.util.Queue;
import java.util.Scanner;

import commands.DepHLC;
import commands.LocoHLC;
import commands.MidLevelCommand;
import planning_modules.Deposition;
import planning_modules.Locomotion;

public class PlanningModuleRunner {

	public static void main(String[] args) {

		System.out.println("Please enter the number of the type of high level command you wish to execute"
				+ "\n(1) Deposition\n(2) Locomotion\n(3) Excavation - unfinished\n");

		int inputInt = inputErrorCheck(new int[] { 1, 2 });

		if (inputInt == 1) {
			System.out.println(
					"\n----Deposition Mode----\nPlease enter type of dump:\n(1) Dump All \n(2) Specific Duration\n(3) Specific Remaining Amount\n"
							+ "Current bin position: 0\nCurrent bin load: 60 kg");

			inputInt = inputErrorCheck(new int[] { 1, 2, 3 });

			if (inputInt == 1) {
				printQueue(Deposition.receiveCommand(new DepHLC()));
			} else if (inputInt == 2) {
				System.out.println("\nPlease enter the desired duration of the dump:\n");
				float fracDuration = floatInput();
				int intDuration = (int) fracDuration;
				fracDuration -= intDuration;

				printQueue(Deposition.receiveCommand(new DepHLC(intDuration, fracDuration)));

			} else if (inputInt == 3) {
				System.out.println("\nPlease enter the desired remaining amount in the bin:\n");
				float amount = floatInput();

				printQueue(Deposition.receiveCommand(new DepHLC(amount)));
			}

		}
		else if (inputInt == 2){
			System.out.println("\nJason hasn't finished his locomotion code yet so I'm just outputting a sample of random turning and moving commands");
			
			printQueue(Locomotion.receiveCommand(new LocoHLC(1, 2, 3)));
			
		}

	}

	public static int inputErrorCheck(int[] validEntries) {
		int inputInt = 0;
		boolean Error = true;

		Scanner scan = new Scanner(System.in);

		while (Error) {
			inputInt = scan.nextInt();

			for (int a : validEntries) {
				Error = inputInt == a ? false : true;
				if (!Error)
					break;
			}

			if (Error)
				System.err.println("Invalid entry, please enter '1' or '2'\n");
		}

		return inputInt;
	}

	public static float floatInput() {
		Scanner scan = new Scanner(System.in);

		float output = 0;
		boolean Error = true;

		while (Error) {
			try {
				output = scan.nextFloat();
			} catch (InputMismatchException e) {
				System.err.println("Invalid entry, please enter a float number\n");
				Error = false;
			}

			Error = !Error ? true : false;
		}

		return output;

	}

	/**
	 * Prints out the MidLevelCommands in the given Queue;
	 */
	public static void printQueue(Queue<MidLevelCommand> q) {
		System.out.println("\nCommand List:");
		int counter = 1;
		for (MidLevelCommand c : q) {
			System.out.println(counter + ") " + c.toString());
			counter++;
		}
	}

}
