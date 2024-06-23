package server.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.server.models.Category;
import server.server.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/allCategories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategorys();
    }
}
