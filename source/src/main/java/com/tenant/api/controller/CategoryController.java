package com.tenant.api.controller;

import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.category.CategoryDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.category.CreateCategoryForm;
import com.tenant.api.form.category.UpdateCategoryForm;
import com.tenant.api.mapper.CategoryMapper;
import com.tenant.api.storage.tenant.criteria.CategoryCriteria;
import com.tenant.api.storage.tenant.model.Category;
import com.tenant.api.storage.tenant.repository.CategoryRepository;
import com.tenant.api.storage.tenant.repository.MovieRepository;
import com.tenant.api.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private MovieRepository movieRepository;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateCategoryForm form) {
        String slug = StringUtils.slugify(form.getName());
        Category category = categoryRepository.findFirstBySlug(slug).orElse(null);
        if (category != null) {
            throw new BadRequestException("[Category] Name existed", ErrorCode.CATEGORY_ERROR_NAME_EXISTED);
        }
        category = categoryMapper.fromCreateCategoryFormToEntity(form);
        category.setSlug(slug);
        categoryRepository.save(category);
        return makeSuccessResponse("Create category success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_V')")
    public ApiMessageDto<CategoryDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<CategoryDto> apiMessageDto = new ApiMessageDto<>();
        Category serviceCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Category] Not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

        apiMessageDto.setData(categoryMapper.entityToCategoryDto(serviceCategory));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get service category success.");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_L')")
    public ApiMessageDto<ResponseListDto<List<CategoryDto>>> list(CategoryCriteria criteria, Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(criteria.getSpecification(), pageable);

        List<CategoryDto> categoryDtoList = categoryMapper.fromEntityToCategoryDtoList(categories.getContent());

        ResponseListDto<List<CategoryDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(categoryDtoList);
        responseListObj.setTotalPages(categories.getTotalPages());
        responseListObj.setTotalElements(categories.getTotalElements());
        return makeSuccessResponse(responseListObj, "List category success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateCategoryForm form) {
        Category category = categoryRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Category] Not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
        String slug = StringUtils.slugify(form.getName());
        if (!Objects.equals(category.getSlug(), slug) && categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("[Category] Name existed", ErrorCode.CATEGORY_ERROR_NAME_EXISTED);
        }
        categoryMapper.fromUpdateCategoryFormToEntity(form, category);
        category.setSlug(slug);
        categoryRepository.save(category);
        return makeSuccessResponse("Update category success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Category] Not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

        if (movieRepository.existsByCategories_Id(category.getId())) {
            throw new BadRequestException("[Category] Cannot delete, still linked to movies", ErrorCode.CATEGORY_ERROR_HAS_MOVIE);
        }

        categoryRepository.delete(category);
        return makeSuccessResponse("Delete category success");
    }
}
