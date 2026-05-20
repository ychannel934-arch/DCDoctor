package com.dcdoctor.UI;

import com.dcdoctor.database.DBConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class NotificationUI extends JFrame {

    JTextArea area;

    public NotificationUI() {
        setTitle("Notifications");
        setSize(400,300);

        area = new JTextArea();
        add(new JScrollPane(area));

        loadData();
    }

    void loadData(){
        area.setText("");

        try(Connection conn = DBConnection.connect()){
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM notifications");

            while(rs.next()){
                area.append(
                        rs.getString("message") +
                                " | Date: " + rs.getString("date") + "\n"
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}