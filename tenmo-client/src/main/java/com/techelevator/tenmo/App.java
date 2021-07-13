package com.techelevator.tenmo;

import com.techelevator.tenmo.auth.models.AuthenticatedUser;
import com.techelevator.tenmo.auth.models.User;
import com.techelevator.tenmo.auth.models.UserCredentials;
import com.techelevator.tenmo.auth.services.AuthenticationService;
import com.techelevator.tenmo.auth.services.AuthenticationServiceException;
import com.techelevator.tenmo.exceptions.AccountNotFoundException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.exceptions.TransferNotFoundException;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;
import org.springframework.core.io.Resource;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import javax.xml.transform.TransformerConfigurationException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService tenmoService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;

	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		tenmoService = new TenmoService(API_BASE_URL, currentUser);
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
		try {
			long userId = currentUser.getUser().getId();
			Account account = tenmoService.getBalance();
			console.printUserBalance(account.getBalance());
		} catch (AccountNotFoundException e) {
			console.accountNotFound();
		} catch (ResourceAccessException e) {
			console.errorCannotConnect();
		} catch (RestClientResponseException e) {
			console.errorClientException(e.getRawStatusCode(), e.getMessage());
		}
		
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		User user = null;
		try {
			List<Transfer> transferList = tenmoService.userTransfers();
			console.printFormatViewTransfers();
			for (Transfer transfer : transferList) {
				console.printUserTransfers(transfer.getTransferId(), currentUser.getUser().getUsername(), transfer.getAmount());
			}
			console.lines();
			long transfer = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
			Transfer selectedTransfer = tenmoService.transferById(transfer);
			if (selectedTransfer.getTransferTypeId() == 2) {
				selectedTransfer.setTransferType("Send");
			} else {
				selectedTransfer.setTransferType("Receive");
			}
			if (selectedTransfer.getTransferStatusId() == 2) {
				selectedTransfer.setTransferStatus("Approved");
			} else if (selectedTransfer.getTransferStatusId() == 1) {
				selectedTransfer.setTransferStatus("Pending");
			} else {
				selectedTransfer.setTransferStatus("Rejected");
			}
			console.printFormatViewTransferDetails(selectedTransfer.getTransferId(), currentUser.getUser().getUsername(), tenmoService.accountToUsernameById(selectedTransfer.getTransferId()),
					selectedTransfer.getTransferType(), selectedTransfer.getTransferStatus(), selectedTransfer.getAmount());
		} catch (TransferNotFoundException e) {
			console.transferNotFound();
		} catch (AccountNotFoundException e) {
			console.accountNotFound();
		} catch (ResourceAccessException e) {
			console.errorCannotConnect();
		} catch (RestClientResponseException e) {
			console.errorClientException(e.getRawStatusCode(), e.getMessage());
		}
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		try {
			List<User> userList = tenmoService.findAllUsers();
			console.printFormatTEBucks();
			for (User user : userList) {
				console.printAvailableUsers(user.getId(), user.getUsername());
			}
			console.lines();
			long userid = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
			BigDecimal transferAmount = BigDecimal.valueOf(console.getUserInputInteger("Enter amount"));
			tenmoService.sendTransfer(userid, transferAmount);
		} catch (TransferNotFoundException e) {
			console.transferNotFound();
		} catch (ResourceAccessException e) {
			console.errorCannotConnect();
		} catch (RestClientResponseException e) {
			console.errorClientException(e.getRawStatusCode(), e.getMessage());
		} catch (InsufficientFundsException e) {
			console.insufficientFunds();
		}
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
