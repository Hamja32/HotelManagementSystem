package HotelManagement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {

	private static final String url =  "jdbc:mysql://localhost:3306/hotel_db";
	private static final String user = "root";
	private static final String pass = "password@hamzakhan";
	
	
	
	public static void main(String[] args)throws ClassNotFoundException, SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Connection con = DriverManager.getConnection(url,user,pass);
			while(true) {
				System.out.println();	
				System.out.println("HOTEL MANAGEMENT SYSTEM");		
				Scanner sc = new Scanner(System.in);
				
				System.out.println("1. Reserv a room");	
				System.out.println("2. View Reservation");	
				System.out.println("3. Get Room Number");	
				System.out.println("4. Update Reservation");	
				System.out.println("5. Delete Reservation");	
				System.out.println("0. Exit");	
				System.out.print("Choose an option: ");
				int choice = sc.nextInt();
				
				switch(choice) {
				case 1:
					reserveRoom(con,sc);
					break;
				
				case 2:
					viewReservations(con);
					break;
					
				case 3:
					getRoomNumber(con,sc);
					break;
					
				case 4:
					updateReservation(con,sc);
					break;
					
				case 5:
					deleteReservation(con,sc);
					break;
					
				case 0:
					exit();
					sc.close();
					break;
					
				default:
					System.out.println("Invaildi choice. Try Again.");
				}
				
			
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());	
		}catch(InterruptedException e) {
			throw new RuntimeException(e);
		}

	}
	
	
	public static void reserveRoom(Connection con,Scanner scanner) {
		try {
			System.out.print("Enter guest name: ");
			String guestName = scanner.next();
			scanner.nextLine();
			
			System.out.print("Enter room number: ");
			int roomNumber = scanner.nextInt();
			
			System.out.print("Enter contact number: ");
			String contactNumber = scanner.next();
			
			String sql = "INSERT INTO reservation (guest_name,room_number,contact_number) " + 
			"values ('" + guestName + "', '" + roomNumber + "', '" + contactNumber + "')";
			
			try(Statement statement = con.createStatement()){
				int affectedRows = statement.executeUpdate(sql);
				if(affectedRows >0) {
					System.out.println("Reservation Successfull");
				}else {
					System.out.println("Reservation failed");

				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void viewReservations(Connection con) throws SQLException {
		String query = "SELECT * FROM reservation";
		
		try(Statement stmt = con.createStatement();
			ResultSet res = stmt.executeQuery(query)){
			
			System.out.println("Current Reservation: ");
			System.out.println("------------------+-----------------------------+---------------------------+------------------------+-----------------------------+");
			System.out.println("RESERVATION ID    |  GUEST NAME                 | ROOM NUMBER               | MOBILE NUMBER          | RESERVATION DATE            |");
			System.out.println("------------------+-----------------------------+---------------------------+------------------------+-----------------------------+");
			
		while(res.next()) {
			
			int reservation_id = res.getInt("reservation_id");
			String guest_name = res.getString("guest_name");
			int room_number = res.getInt("room_number");
			String contact_number = res.getString("contact_number");
			String reservation_date = res.getTimestamp("reservation_date").toString();
			
			
			System.out.printf(" %-15d  | %-26s  | %-23d  | %-22s  | %-26s  | \n",
					reservation_id,guest_name,room_number,contact_number,reservation_date);
		}
		
		System.out.printf(".........................................................................................................................................");
		
		}catch(Exception e) {
			e.getMessage();
		}
	}
	
	public static void getRoomNumber(Connection con, Scanner sc) {
		try {
			System.out.print("Enter reservation id: ");
			int reservation_id = sc.nextInt();
			System.out.print("Enter guest name: ");
			String guest_name = sc.next();
			
			
			String sql = "SELECT room_number FROM reservation WHERE reservation_id = " + reservation_id + " AND guest_name = '" +  guest_name + "'" ;
			try(Statement stmt = con.createStatement(); 
				ResultSet res = stmt.executeQuery(sql)){
				if(res.next()) {
					int roomNumber = res.getInt("room_number");
					System.out.println("\nRoom number for reservation_id " + reservation_id + " and Guest " + guest_name + " is : " + roomNumber);
				}else {
					System.out.println("Reservation not found for the given ID and Gust Name. ");
				}
				
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void updateReservation(Connection con, Scanner scanner) {
		try {
			System.out.print("Enter reservation ID to Update: ");
			int reservation_id = scanner.nextInt();
			scanner.nextLine();
			
			if(!reservationExist(con,reservation_id)) {
				System.out.println("Reservation not found for the given ID : ");
				return;
			}
			
			System.out.println("Enter new guest name: ");
			String new_guest_name = scanner.nextLine();
			System.out.println("Enter new room number: ");
			String new_room_number = scanner.next();
			System.out.println("Enter new contact number: ");
			String new_contact_number = scanner.next();
			
			String sql = "UPDATE reservation SET guest_name = '" +new_guest_name+ "'," +
			"room_number = " + new_room_number + ", " +
			"contact_number = '" + new_contact_number + "' " +
			"WHERE reservation_id = " + reservation_id ;
			
			try(Statement stmt = con.createStatement()) {
				int affectedRows = stmt.executeUpdate(sql);
				if(affectedRows > 0) {
					System.out.println("Reservation updated successfully! ");
				}else {
					System.out.println("Reservation updated failed! ");
				}
				
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void deleteReservation(Connection con, Scanner scanner) {
		try {
			System.out.print("Enter reservation ID for delete: ");
			int reservation_id = scanner.nextInt();
			scanner.nextLine();
			
			if(!reservationExist(con,reservation_id)) {
				System.out.println("Reservation not found for the given ID : ");
				return;
			}
			
			
			
			String sql = "DELETE FROM reservation WHERE reservation_id = " + reservation_id;
			
			try(Statement stmt = con.createStatement()) {
				int affectedRows = stmt.executeUpdate(sql);
				if(affectedRows > 0) {
					System.out.println("Reservation deleted successfully! ");
				}else {
					System.out.println("Reservation deleted failed! ");
				}
				
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean reservationExist(Connection con, int reservation_id) {
		try {
			String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservation_id;
			
			try(Statement stmt = con.createStatement();
				ResultSet res = stmt.executeQuery(sql) ) {
				
				return res.next();
				
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void exit() throws InterruptedException{
		System.out.println("Exiting System");
		int i = 5;
		while(i!=0) {
			System.out.print(".");
			Thread.sleep(500);
			i++;
		}
		System.out.println();
		System.out.println("Thank You for using Hotel Reservation System.");
	}
	

}
