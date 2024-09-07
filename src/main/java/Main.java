import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Bookshop Evolution Project!");
        System.out.println("This project demonstrates 5 different ways to implement a Bookshop system in Java.");
        System.out.println("Please choose a version to explore (1-5):");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("V1 - HashSet Implementation -> use the command: git checkout branch 'V1-hashset'");
                break;
            case 2:
                System.out.println("V2 - CSV Implementation -> use the command: git checkout branch 'V2-IO'");
                break;
            case 3:
                System.out.println("V3 - JDBC Implementation use the command: git checkout branch 'V3-jdbc'");
                break;
            case 4:
                System.out.println("V4 - Servlet & DAO Implementation use the command: git checkout branch 'V4-Servlet'");
                break;
            case 5:
                System.out.println("V5 - Spring Boot Implementation use the command: git checkout branch 'V5-SpringBoot'");
                break;
            default:
                System.out.println("Invalid choice. Please run again and select a number between 1 and 5.");
        }
    }
}