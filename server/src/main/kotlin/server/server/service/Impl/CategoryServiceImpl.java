package server.server.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.server.models.Category;
import server.server.repository.CategoryRepository;
import server.server.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Override
    public List<Category> getAllCategorys() {
        return (List<Category>) categoryRepository.findAll();
    }
}
