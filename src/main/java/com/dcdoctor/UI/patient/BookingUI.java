package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BookingUI extends JFrame {

    JTextField txtDoctor, txtDate;

    public BookingUI() {
        setTitle("Booking");
        setSize(300,200);
        setLayout(new FlowLayout());

        add(new JLabel("Doctor:"));
        txtDoctor = new JTextField(15);
        add(txtDoctor);

        add(new JLabel("Date:"));
        txtDate = new JTextField(15);
        add(txtDate);

        JButton btn = new JButton("Book");
        add(btn);

        btn.addActionListener(e -> saveBooking());
    }

    void saveBooking(){
        try(Connection conn = DBConnection.connect()){
            String sql = "INSERT INTO appointments (patient_id, doctor_name, date) VALUES (?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, 1); // demo patient
            ps.setString(2, txtDoctor.getText());
            ps.setString(3, txtDate.getText());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Booked!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}