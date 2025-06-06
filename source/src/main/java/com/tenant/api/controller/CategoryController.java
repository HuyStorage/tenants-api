package com.tenant.api.controller;


import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.storage.tenant.model.Category;
import com.tenant.api.dto.category.CategoryDto;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.mapper.CategoryMapper;
import com.tenant.api.storage.tenant.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CA_V')")
    public ApiMessageDto<CategoryDto> getServiceCategory(@PathVariable("id") Long id) {
        if(!isShop() && !isSuperAdmin()){
            throw new UnauthorizationException("Not allowed get.");
        }
        ApiMessageDto<CategoryDto> apiMessageDto = new ApiMessageDto<>();
        Category serviceCategory = categoryRepository.findById(id).orElse(null);
        if(serviceCategory == null){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            return apiMessageDto;
        }

        apiMessageDto.setData(categoryMapper.entityToACategoryAllDto(serviceCategory));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get service category success.");
        return  apiMessageDto;
    }

}
