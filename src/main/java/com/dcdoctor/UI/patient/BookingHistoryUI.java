package com.dcdoctor.UI.patient;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BookingHistoryUI extends JFrame {

    JTextArea area;

    public BookingHistoryUI() {
        setTitle("Booking History");
        setSize(400,300);

        area = new JTextArea();
        add(new JScrollPane(area));

        loadData();
    }

    void loadData(){
        try(Connection conn = DBConnection.connect()){
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM appointments");

            while(rs.next()){
                area.append(
                        "Doctor: " + rs.getString("doctor_name") +
                                " | Date: " + rs.getString("date") + "\n"
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}