package com.ousl.util;

public class LoggedUser {

    private static boolean userLogged = false;
    private static String SNo;
    private static int RegNo;
    private static String NIC;
    private static String Name = "Guest";
    private static String Email = "You are not logged in.";
    private static int Contact;

    public static void setUserLogged(boolean isLogged){
        userLogged = isLogged;
    }

    public static boolean getUserLogged(){
        return userLogged;
    }

    public static void setSNo(String sno){
        SNo = sno;
    }

    public static String getSNo(){
        return SNo;
    }

    public static void setRegNo(int regno){
        RegNo = regno;
    }

    public static int getRegNo(){
        return RegNo;
    }

    public static void setNIC(String nic){
        NIC = nic;
    }

    public static String getNIC(){
        return NIC;
    }

    public static void setName(String name){
        Name = name;
    }

    public static String getName(){
        return Name;
    }

    public static void setEmail(String email){
        Email = email;
    }

    public static String getEmail(){
        return Email;
    }

    public static void setContact(int contact){
        Contact = contact;
    }

    public static int getContact(){
        return Contact;
    }

}
