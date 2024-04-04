package com.example.admin.controller;

import com.libary.dto.ProductDto;
import com.libary.model.Category;
import com.libary.service.CategoryService;
import com.libary.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String PRODUCTS_VIEW = "products";
    private static final String PRODUCT_FORM_VIEW = "update-product";
    private static final String REDIRECT_PRODUCTS = "redirect:/products";

    @GetMapping("/products")
    public String viewProducts(Model model, Principal user) {
        return processUser(user, () -> {
            List<ProductDto> productList = productService.allProduct();
            model.addAttribute("products", productList);
            model.addAttribute("size", productList.size());
            return PRODUCTS_VIEW;
        });
    }


// issue with the search in products


    @GetMapping("/add-product")
    public String addProductPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Add Product");
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", new ProductDto());
        return "add-product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("productDto") ProductDto product, @RequestParam("imageProduct") MultipartFile image, RedirectAttributes attributes, Principal user) {
        return processUser(user, () -> {
            try {
                productService.save(image, product);
                attributes.addFlashAttribute("success", "Add new product successfully!");
            } catch (Exception e) {
                handleError(e, attributes);
            }
            return REDIRECT_PRODUCTS;
        });
    }

    @GetMapping("/update-product/{id}")
    public String updateProductForm(@PathVariable Long id, Model model, Principal user) {
        return processUser(user, () -> {
            ProductDto productDto = productService.getById(id);
            addProductFormAttributes(model, productDto);
            return PRODUCT_FORM_VIEW;
        });
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto, @RequestParam("imageProduct") MultipartFile image, RedirectAttributes attributes, Principal user) {
        return processUser(user, () -> {
            try {
                productService.update(image, productDto);
                attributes.addFlashAttribute("success", "Update successfully!");
            } catch (Exception e) {
                handleError(e, attributes);
            }
            return REDIRECT_PRODUCTS;
        });
    }

    @RequestMapping(value = "/enable-product", method = {RequestMethod.PUT, RequestMethod.GET})
    public String enableProduct(Long id, RedirectAttributes attributes, Principal user) {
        return processUser(user, () -> {
            try {
                productService.enableById(id);
                attributes.addFlashAttribute("success", "Enabled successfully!");
            } catch (Exception e) {
                handleError(e, attributes);
            }
            return REDIRECT_PRODUCTS;
        });
    }

    @RequestMapping(value = "/delete-product", method = {RequestMethod.PUT, RequestMethod.GET})
    public String deleteProduct(Long id, RedirectAttributes attributes, Principal user) {
        return processUser(user, () -> {
            try {
                productService.deleteById(id);
                attributes.addFlashAttribute("success", "Deleted successfully!");
            } catch (Exception e) {
                handleError(e, attributes);
            }
            return REDIRECT_PRODUCTS;
        });
    }

    private void addPaginationAttributes(Model model, int currentPage, Page<ProductDto> page) {
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", page.getSize());
        model.addAttribute("products", page);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
    }

    private void addProductFormAttributes(Model model, ProductDto productDto) {
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", productDto);
    }

    private String processUser(Principal user, Supplier<String> action) {
        if (user == null) {
            return REDIRECT_LOGIN;
        }
        return action.get();
    }

    private void handleError(Exception e, RedirectAttributes attributes) {
        e.printStackTrace();
        attributes.addFlashAttribute("error", "An error occurred!");
    }

    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }
}
