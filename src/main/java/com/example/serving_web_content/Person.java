package com.example.serving_web_content;

public class Person {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String birthday; // Assuming birthday is stored as a String (consider using LocalDate for date handling)
    private double salary;
    private double bonus;

    public Person(int id, String firstName, String lastName, String email, String country, String birthday, double salary, double bonus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.birthday = birthday;
        this.salary = salary;
        this.bonus = bonus;
    }

    // Getters and setters for all attributes (id, firstName, lastName, email, country, birthday, salary, bonus)

    // Optional: Override toString() method to provide a meaningful representation of the Person object
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", birthday='" + birthday + '\'' +
                ", salary=" + salary +
                ", bonus=" + bonus +
                '}';
    }
}
