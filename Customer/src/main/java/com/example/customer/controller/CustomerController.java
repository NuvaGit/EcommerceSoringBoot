package com.example.customer.controller;

import com.libary.dto.CustomerDto;
import com.libary.model.City;
import com.libary.model.Country;
import com.libary.service.CityService;
import com.libary.service.CountryService;
import com.libary.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.function.Supplier;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final CountryService countryService;
    private final CityService cityService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        return redirectToLoginIfUnauthenticated(principal, () -> {
            var customer = customerService.getCustomer(principal.getName());
            setupModelForProfile(model, customer);
            return "customer-information";
        });
    }

    @PostMapping("/update-profile")
    public String updateProfile(@Valid @ModelAttribute("customer") CustomerDto customerDto,
                                BindingResult result, RedirectAttributes redirectAttributes,
                                Model model, Principal principal) {
        return redirectToLoginIfUnauthenticated(principal, () -> {
            if (result.hasErrors()) {
                setupModelForProfile(model, customerDto);
                return "customer-information";
            }
            customerService.update(customerDto);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/profile";
        });
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model) {
        model.addAttribute("title", "Change Password");
        model.addAttribute("page", "Change Password");
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePass(@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             @RequestParam("repeatNewPassword") String repeatPassword,
                             RedirectAttributes attributes,
                             Model model,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            CustomerDto customer = customerService.getCustomer(principal.getName());
            if (passwordEncoder.matches(oldPassword, customer.getPassword())
                    && !passwordEncoder.matches(newPassword, oldPassword)
                    && !passwordEncoder.matches(newPassword, customer.getPassword())
                    && repeatPassword.equals(newPassword) && newPassword.length() >= 5) {
                customer.setPassword(passwordEncoder.encode(newPassword));
                customerService.changePass(customer);
                attributes.addFlashAttribute("success", "Your password has been changed successfully!");
                return "redirect:/profile";
            } else {
                model.addAttribute("message", "Your password is wrong");
                return "change-password";
            }
        }
    }


    private void setupModelForProfile(Model model, Object customer) {
        model.addAttribute("customer", customer);
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("countries", countryService.findAll());
        model.addAttribute("title", "Profile");
        model.addAttribute("page", "Profile");
    }

    private String redirectToLoginIfUnauthenticated(Principal principal, Supplier<String> authenticatedAction) {
        if (principal == null) {
            return "redirect:/login";
        }
        return authenticatedAction.get();
    }
}
