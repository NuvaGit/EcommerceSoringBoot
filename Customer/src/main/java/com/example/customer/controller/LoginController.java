package com.example.customer.controller;

import com.libary.dto.CustomerDto;
import com.libary.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class LoginController {
    private final CustomerService customerService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String LOGIN_VIEW = "login";
    private static final String REGISTER_VIEW = "register";
    private static final String CUSTOMER_DTO_MODEL_KEY = "customerDto";
    private static final String TITLE_MODEL_KEY = "title";
    private static final String PAGE_MODEL_KEY = "page";
    private static final String ERROR_MODEL_KEY = "error";
    private static final String SUCCESS_MODEL_KEY = "success";

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute(TITLE_MODEL_KEY, "Login Page");
        model.addAttribute(PAGE_MODEL_KEY, LOGIN_VIEW);
        return LOGIN_VIEW;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute(TITLE_MODEL_KEY, "Register");
        model.addAttribute(PAGE_MODEL_KEY, REGISTER_VIEW);
        model.addAttribute(CUSTOMER_DTO_MODEL_KEY, new CustomerDto());
        return REGISTER_VIEW;
    }

    @PostMapping("/do-register")
    public String registerCustomer(@Valid @ModelAttribute(CUSTOMER_DTO_MODEL_KEY) CustomerDto customerDto,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            return returnWithError(model, REGISTER_VIEW, "Please correct the errors in the form.");
        }

        if (!customerDto.getPassword().equals(customerDto.getConfirmPassword())) {
            return returnWithError(model, REGISTER_VIEW, "Passwords do not match.");
        }

        if (customerService.findByUsername(customerDto.getUsername()) != null) {
            return returnWithError(model, REGISTER_VIEW, "Username is already in use.");
        }

        return registerAndRespond(customerDto, model);
    }

    private String registerAndRespond(CustomerDto customerDto, Model model) {
        try {
            customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
            customerService.save(customerDto);
            model.addAttribute(SUCCESS_MODEL_KEY, "Registration successful!");
            return REGISTER_VIEW;
        } catch (Exception e) {
            return returnWithError(model, REGISTER_VIEW, "An error occurred during registration. Please try again later.");
        }
    }

    private String returnWithError(Model model, String viewName, String errorMessage) {
        model.addAttribute(ERROR_MODEL_KEY, errorMessage);
        return viewName;
    }
}
