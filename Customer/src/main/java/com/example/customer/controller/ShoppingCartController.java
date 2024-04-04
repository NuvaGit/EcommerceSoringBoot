package com.example.customer.controller;

import com.libary.dto.ProductDto;
import com.libary.model.ShoppingCart;
import com.libary.service.CustomerService;
import com.libary.service.ProductService;
import com.libary.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.function.Supplier;

@Controller
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;

    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session) {
        if (principal == null) {
            return "redirect:/login";
        }
        var cart = customerService.findByUsername(principal.getName()).getCart();
        model.addAttribute("shoppingCart", cart);
        model.addAttribute("title", "Cart");
        session.setAttribute("totalItems", cart != null ? cart.getTotalItems() : 0);
        model.addAttribute("grandTotal", cart != null ? cart.getTotalPrice() : 0);
        return "cart";
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("id") Long id,
                                @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
                                HttpServletRequest request,
                                Principal principal,
                                HttpSession session) {
        return handleCartUpdate(principal, session, () -> cartService.addItemToCart(productService.getById(id), quantity, principal.getName()), request.getHeader("Referer"));
    }

    @PostMapping(value = "/update-cart", params = "action=update")
    public String updateCart(@RequestParam("id") Long id,
                             @RequestParam("quantity") int quantity,
                             Principal principal,
                             HttpSession session) {
        return handleCartUpdate(principal, session, () -> cartService.updateCart(productService.getById(id), quantity, principal.getName()), "/cart");
    }

    @PostMapping(value = "/update-cart", params = "action=delete")
    public String deleteItem(@RequestParam("id") Long id,
                             Principal principal,
                             HttpSession session) {
        return handleCartUpdate(principal, session, () -> cartService.removeItemFromCart(productService.getById(id), principal.getName()), "/cart");
    }

    private String handleCartUpdate(Principal principal, HttpSession session, Supplier<ShoppingCart> cartOperation, String redirectUrl) {
        if (principal == null) {
            return "redirect:/login";
        }
        var shoppingCart = cartOperation.get();
        session.setAttribute("totalItems", shoppingCart.getTotalItems());
        return "redirect:" + redirectUrl;
    }
}
