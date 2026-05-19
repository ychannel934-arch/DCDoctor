package com.dcdoctor.database;

import com.dcdoctor.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // INSERT (AUTO INCREMENT nên không cần id)
    public void insertPatient(Patient p) {
        String sql = "INSERT INTO users(name, email) VALUES (?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getFullName());
            stmt.setString(2, p.getEmail());

            stmt.executeUpdate();
            System.out.println("Inserted patient!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SELECT ALL
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Patient p = new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "Unknown"
                );
                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // UPDATE
    public void updatePatient(int id, String newName, String newEmail) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Updated patient!");
            } else {
                System.out.println("Không tìm thấy ID!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void deletePatient(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Deleted patient!");
            } else {
                System.out.println("Không tìm thấy ID!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}