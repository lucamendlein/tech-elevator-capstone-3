package com.techelevator.tenmo.services;


import com.techelevator.tenmo.models.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}

	public void printUserBalance(BigDecimal prompt){
		System.out.println("Your current account balance is : $" + prompt);
	}

	public void printAvailableUsers(long prompt1, String prompt2) {

		System.out.printf("%-10s %-12s", prompt1, prompt2);
		System.out.println();
	}
	public void printUserTransfers(long input1, String input2, BigDecimal input3){
		System.out.printf("%-10s %-12s %-20s", input1, "From: " + input2, "$ " + input3);
		System.out.println();
	}

	public void printTransferDetails(long prompt1){
		System.out.println(prompt1);
	}

	public void inputPrompt(String input){
		System.out.println(input);
	}

	public void bigDecimalPrompt(BigDecimal input) {
		System.out.println(input);
	}

	public void errorCannotConnect() {
		System.out.println("Cannot connect to server.");
	}

	public void errorClientException(int statusCode, String message) {
		System.out.println(statusCode + " " + message);
	}

	public void transferNotFound() {
		System.out.println("Transfer not found.");
	}

	public void accountNotFound() {
		System.out.println("Account not found.");
	}

	public void insufficientFunds() {
		System.out.println("Insufficient funds.");

	}

	public void printFormatTEBucks() {
		lines();
		System.out.println();
		System.out.printf("%-10s", "Users");
		System.out.println();
		System.out.printf("%-10s %-12s", "ID", "Name" );
		System.out.println();
		lines();
		System.out.println();
	}

	public void printFormatViewTransfers() {
		lines();
		System.out.println();
		System.out.printf("%-10s", "Transfers");
		System.out.println();
		System.out.printf("%-10s %-12s %-20s", "ID", "From/To", "Amount");
		System.out.println();
		lines();
		System.out.println();

	}

	public void printFormatViewTransferDetails(long input1, String input2, String input3, String input4, String input5, BigDecimal input6) {
		lines();
		System.out.println();
		System.out.printf("%-10s", "Transfer Details");
		System.out.println();
		lines();
		System.out.println();
		System.out.printf("%-12s", "ID: " + input1);
		System.out.println();
		System.out.printf("%-12s", "From: " + input2);
		System.out.println();
		System.out.printf("%-12s", "To: " + input3);
		System.out.println();
		System.out.printf("%-12s", "Type: " + input4);
		System.out.println();
		System.out.printf("%-12s", "Status: " + input5);
		System.out.println();
		System.out.printf("%-12s", "Amount: $" + input6);
		System.out.println();
	}

	public void lines() {
		System.out.println("-------------------------------------------");
	}
}
