package com.example.login.Controller;

import com.example.login.Model.User;
import com.example.login.Repository.UserRepository;
import com.example.login.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.List;

@Controller
@SessionAttributes
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    //Landing page of website
    @RequestMapping("/")
    public String landingPage(Model model)
    {
        return "page";
    }
    @RequestMapping("/admin")
    public String landingPage1(Model model)
    {
        return "admin";
    }
    @RequestMapping("/user")
    public String landingPage2(Model model)
    {
        return "user";
    }
    @RequestMapping("/officer")
    public String landingPage3(Model model)
    {
        return "officer";
    }


    //Function for register
    @PostMapping("/register")
    public String submit(@RequestParam("name") String name,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password, HttpSession session)
    {
        if(userService.checkEmail(email))
        {
            //session.setAttribute("msg", "Email id already exist");
            session.setAttribute("registrationSuccess", false);
            session.setAttribute("registrationError", true);
        }
        else {
            User user=new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            userRepository.save(user);
            System.out.println("Data Saved");
            //session.setAttribute("msg", "Registered");
            session.setAttribute("registrationSuccess", true);
            session.setAttribute("registrationError", false);
        }

        return "redirect:/";

    }

    @Autowired
    private UserService userService;


    @Autowired
    private ObjectMapper objectMapper;

    //Function for login
    @PostMapping ("/log")
    public String loginHomepage(@RequestParam("email1") String userName,
                                @RequestParam("password1") String password, Model model) {
        User u = null;
        //User p = null;
        try {
            u = userRepository.findByEmail(userName);
            //p = userRepository.findByPassword(password);
        } catch (Exception e) {
            System.out.println("User not found !!!");
        }
        if(u==null)
        {
            // User not found in the database
            return "redirect:/login/errorUserNotFound";

        }
        else if(!u.getPassword().equals(password))
        {
            // Password does not match
            return "redirect:/login/errorIncorrectPassword";
        }
        else
        //if (u != null && u.getPassword().equals(password))
        {
            model.addAttribute("USERNAME", userName);
            String role = u.getRole();
            //If user is admin
            if (role != null && role.equals("admin")) {


                return "redirect:http://localhost:9090/admin";
            }
            //If user is doctor
            else if (role != null && role.equals("officer")) {


                return "officer";
            }
            //If user is patient
            else {


                return "user";
            }
            //return "redirect:http://localhost:8888/appointment/patient";
        }
        //model.addAttribute("error", "User Not Found, Kindly register!!");

        //ResponseEntity<String> entity= new ResponseEntity<>(userName, HttpStatus.OK);

        //return "page";
    }

    @GetMapping("/login/errorUserNotFound")
    public String errorUserNotFound(Model model) {
        model.addAttribute("errorMessage", "User not found. Please register!");
        return "error";
    }

    @GetMapping("/login/errorIncorrectPassword")
    public String errorIncorrectPassword(Model model) {
        model.addAttribute("errorMessage", "Incorrect password. Please try again!");
        return "error";
    }

//    @GetMapping("/login/success")
//    public String loginSuccess() {
//        return "redirect:/home"; // Redirect to home page after successful login
//    }
    //Logout
    @GetMapping("/login/logout")
    public String out(Model model)
    {
        return "page";
    }

    //Update role
    @GetMapping("/update_doctor")
    public String updateDoctor(Model model)
    {
        List<User> lists=userRepository.findAll();
        model.addAttribute("lists",lists);
        return "updateDoctor";
    }

    @PostMapping("/confirmDoctor")
    public String confirmAppointment(@RequestParam("confirmingId") Long id,
                                     @RequestParam("confirmingrole") String role) {

        userService.updateDoctor(id, role);
        return "redirect:/update_doctor";
    }
}
