package project1;

import java.sql.*;
import java.util.Scanner;

public class CollegeApp {
    // change if your DB user/password differ
    private static final String URL = "jdbc:mysql://localhost:3306/college_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // XAMPP default is often empty

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1) Add student  2) Search by blood type  3) List all  4) Exit");
                System.out.print("Choose: ");
                String choice = sc.nextLine().trim();
                if (choice.equals("1")) addStudent(sc);
                else if (choice.equals("2")) searchByBlood(sc);
                else if (choice.equals("3")) listAll();
                else if (choice.equals("4")) { System.out.println("Goodbye."); break; }
                else System.out.println("Invalid choice.");
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static void addStudent(Scanner sc) {
        System.out.print("Name: "); String name = sc.nextLine().trim();
        System.out.print("Age: "); int age = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Department: "); String dept = sc.nextLine().trim();
        System.out.print("Blood type (e.g. A+, O-): "); String blood = sc.nextLine().trim();
        System.out.print("Phone: "); String phone = sc.nextLine().trim();
        System.out.print("Email: "); String email = sc.nextLine().trim();

        String sql = "INSERT INTO students (name, age, department, blood_type, phone, email) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, dept);
            ps.setString(4, blood);
            ps.setString(5, phone);
            ps.setString(6, email);
            int rows = ps.executeUpdate();
            System.out.println(rows == 1 ? "Student added." : "Insert failed.");
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private static void searchByBlood(Scanner sc) {
        System.out.print("Enter blood type to search (e.g. A+): ");
        String blood = sc.nextLine().trim();
        String sql = "SELECT id, name, age, department, phone, email FROM students WHERE blood_type = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, blood);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf("ID:%d | %s | Age:%d | %s | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("department"),
                        rs.getString("phone"),
                        rs.getString("email"));
                }
                if (!found) System.out.println("No students with blood type " + blood);
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private static void listAll() {
        String sql = "SELECT id, name, age, department, blood_type FROM students ORDER BY id";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("ID:%d | %s | %d | %s | %s%n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("department"),
                    rs.getString("blood_type"));
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }
}
