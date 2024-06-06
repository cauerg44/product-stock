package com.appfullstack.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appfullstack.backend.dto.CategoryDTO;
import com.appfullstack.backend.dto.ProductDTO;
import com.appfullstack.backend.entities.Category;
import com.appfullstack.backend.entities.Product;
import com.appfullstack.backend.entities.Supplier;
import com.appfullstack.backend.repositories.ProductRepository;
import com.appfullstack.backend.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(Pageable pageable) {
		Page<Product> products = repository.findAll(pageable);
		return products.map(prod -> new ProductDTO(prod));
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product prod = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Resource not found."));
		return new ProductDTO(prod);
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		dtoToEntity(entity, dto);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	private void dtoToEntity(Product entity, ProductDTO dto) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setManufactureDate(dto.getManufactureDate());
		entity.setRating(dto.getRating());
		
		Supplier supplier = new Supplier();
		if (dto.getSupplier() != null) {
		    supplier.setId(dto.getSupplier().getId());
		}
		entity.setSupplier(supplier);
		
		entity.getCategories().clear();
		for (CategoryDTO categoriesDTO : dto.getCategories()) {
			Category category = new Category();
			category.setId(categoriesDTO.getId());
			entity.getCategories().add(category);
		}
	}
}
