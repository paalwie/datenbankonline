package com.example.serving_web_content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class GreetingController {

    @Autowired
    private JdbcTemplate jdbcTemplate; // Injizierte JdbcTemplate

    @GetMapping("/")
    public String index(Model model) {
        String sql = "SELECT * FROM person"; 
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        model.addAttribute("data", rows);
        return "index";
    }

    @PostMapping("/addPerson")
    public String addPerson(@RequestParam("firstName") String firstName,
                            @RequestParam("lastName") String lastName,
                            @RequestParam("email") String email,
                            @RequestParam("country") String country,
                            @RequestParam("salary") int salary,
                            @RequestParam("bonus") int bonus,
                            @RequestParam("birthday") String birthday) throws ParseException
                            { // Handle parsing exception

        // Convert birthday String to a java.sql.Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format if needed
        java.util.Date parsedDate = dateFormat.parse(birthday);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

        String sql = "INSERT INTO person (first_name, last_name, email, country, birthday, salary, bonus) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, firstName, lastName, email, country, sqlDate, salary, bonus);

        // Optional: Redirect or reload data and show success message
        return "redirect:/"; // Redirect to the main page after adding person
    }

    @PostMapping("/deletePerson")
    public String deletePerson(@RequestParam("id") int id) {
        String sql = "DELETE FROM person WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return "redirect:/"; // Redirect to the main page after deleting person
    }

    @PostMapping("/alleAnzeigen")
    public ModelAndView alleAnzeigen() {
        String sql = "SELECT * FROM person ORDER BY id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("data", rows);
        return modelAndView;
    }

    @PostMapping("/filter")
    public String filter(@RequestParam("filterText") String filterText,
                         @RequestParam("filterOption") String selectedValue,
                         Model model) {

        // Validate filterText (optional)
        if (filterText == null || filterText.isEmpty()) {
            return "redirect:/"; // Or return a view with error message
        }

        // Map filterOption to column name
        String filterColumn = switch (selectedValue) {
            case "vorname" -> "first_name";
            case "nachname" -> "last_name";
            case "land" -> "country";
            case "email" -> "email";
            case "birthday" -> "birthday";
            case "salary" -> "salary";
            case "bonus" -> "bonus";
            default -> throw new IllegalArgumentException("Invalid filter option: " + selectedValue);
        };

        if (selectedValue.equals("salary") || selectedValue.equals("bonus")) {

            if (!filterText.matches("[0-9]+")) { // Validate if filterText is an integer(Integer.parseInt(filterText)) {
                return "redirect:/"; // Or return a view with error message
            }

            int filterValue = Integer.parseInt(filterText);
            String sql = "SELECT * FROM person WHERE " + filterColumn + " = ?";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, filterValue);
            model.addAttribute("data", rows);
            return "index";
        }

        else {
            // Build secure SQL query with prepared statement
            String sql = "SELECT * FROM person WHERE " + filterColumn + " LIKE ?";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, "%" + filterText + "%");

            model.addAttribute("data", rows);
            return "index";
        }
    }

    @PostMapping("/editPerson")
    public String updatePerson(@RequestParam("filterText") String filterText,
                               @RequestParam("filterId") int id, @RequestParam("updateOption") String updateOption,
                               Model model) throws ParseException {

        switch (updateOption) {

            case "birthday":
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); ////

                try {
                    Date date = dateFormat.parse(filterText);
                    System.out.println("Eingabe entspricht dem Format");
                } catch (ParseException e) {
                    System.out.println("Eingabe entspricht NICHT dem Format");
                    break;
                }

                // Adjust format if needed
                java.util.Date parsedDate = dateFormat.parse(filterText);
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                jdbcTemplate.update("UPDATE person SET birthday = ? WHERE id = ?", sqlDate, id);
                break;
            case "bonus":
                try {
                    int bonus = Integer.parseInt(filterText);
                    jdbcTemplate.update("UPDATE person SET bonus = ? WHERE id = ?", bonus, id);
                    break;
                } catch (NumberFormatException e) {
                    break;
                }
            case "salary":
                try {
                    int number = Integer.parseInt(filterText);
                    System.out.println("String contains an integer: " + number);
                } catch (NumberFormatException e) {
                    break;
                }
                int salary = Integer.parseInt(filterText);
                jdbcTemplate.update("UPDATE person SET salary = ? WHERE id = ?", salary, id);
                break;

            case "email":
                jdbcTemplate.update("UPDATE person SET email = ? WHERE id = ?", filterText, id);
                break;
            case "firstName":
                jdbcTemplate.update("UPDATE person SET first_name = ? WHERE id = ?", filterText, id);
                break;
            case "lastName":
                jdbcTemplate.update("UPDATE person SET last_name = ? WHERE id = ?", filterText, id);
                break;
            case "country":
                jdbcTemplate.update("UPDATE person SET country = ? WHERE id = ?", filterText, id);
                break;
            default:
                throw new IllegalArgumentException("Ungültige Update-Option: " + updateOption);
        }

        // Redirect to the main page or display a success message
        return "redirect:/";
    }



    @PostMapping("/sortieren")
    public String sortPeople(@RequestParam("sortOption") String sortOption,
                             @RequestParam("sortieroption") String sortOrder,
                             Model model) {

        String sql = "SELECT * FROM person ORDER BY ";
        switch (sortOption) {
            case "email":
                sql += "LOWER(email)";
                break;
            case "birthday":
                sql += "birthday";
                break;
            case "salary":
                sql += "salary";
                break;
            case "bonus":
                sql += "bonus";
                break;
            case "firstName":
                sql += "LOWER(first_name)";
                break;
            case "lastName":
                sql += "LOWER(last_name)";
                break;
            case "country":
                sql += "LOWER(country)";
                break;
            default:
                // Handle invalid sortOption
        }

        sql += " " + sortOrder; // Add ASC or DESC based on selection

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        model.addAttribute("data", rows);
        return "index";
    }

    @GetMapping("/editPerson/{id}")
    public ModelAndView editPerson(@PathVariable("id") int id) {
        String sql = "SELECT * FROM person WHERE id = ?";

        //zum testen
        Map<String, Object> person = jdbcTemplate.queryForMap(sql, id);
        for (Map.Entry<String, Object> entry : person.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String formattedValue;

            if (value instanceof Integer) {
                formattedValue = String.valueOf((Integer) value);
            } else if (value instanceof String) {
                formattedValue = (String) value;
            } else {
                formattedValue = value.toString(); // Default formatting for other types
            }

            System.out.println(key + " : " + formattedValue);
        }


        // Check if person is null
        if (person == null) {
            return new ModelAndView("error");  // Handle no person found (optional)
        }

        ModelAndView modelAndView = new ModelAndView("editPerson");
        modelAndView.addObject("person", person);
        return modelAndView;
    }

    @PostMapping("/editPersonAll")
    public String updatePersonAll(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
                                  @RequestParam("email") String email, @RequestParam("country") String country,
                                  @RequestParam("birthday") String birthday, @RequestParam("salary") int salary,
                                  @RequestParam("bonus") int bonus, @RequestParam("id") int id) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format if needed
        java.util.Date parsedDate = dateFormat.parse(birthday);
        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

        // Create the SQL update query
        String sqlUpdate = "UPDATE person SET first_name = ?, last_name = ?, email = ?, country = ?, birthday = ?, salary = ?, bonus = ? WHERE id = ?";

        System.out.println("Update person SET first_name = "  + firstName + ", last_name = " + lastName + ", email = " + email + ", country = " + country + ", birthday = " + sqlDate + ", salary = " + salary + ", bonus = " + bonus + " WHERE id = " + id);

        jdbcTemplate.update(sqlUpdate, firstName, lastName, email, country, sqlDate, salary, bonus, id);
        // Redirect to a success page or display a success message
        return "redirect:/";
    }


}
