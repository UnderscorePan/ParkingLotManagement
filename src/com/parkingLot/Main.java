package com.parkingLot;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:database/parking_lot.db";
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC Driver loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found!");
        }

    }
}