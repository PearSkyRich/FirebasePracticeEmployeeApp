package com.example.examlast;

public class Employee {
    private String id;
    private String imageBase64;
    private String fullName;
    private String birthDate;
    private String address;
    private String gender;
    private String email;
    private double salary;
    private String position;

    public Employee() {}

    public Employee(String imageBase64, String fullName, String birthDate, String address, String gender, String email, double salary, String position) {
        this.imageBase64 = imageBase64;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
        this.email = email;
        this.salary = salary;
        this.position = position;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}