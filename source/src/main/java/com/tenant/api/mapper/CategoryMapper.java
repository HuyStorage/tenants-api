package com.tenant.api.mapper;


import com.tenant.api.dto.category.CategoryDto;
import com.tenant.api.form.category.CreateCategoryForm;
import com.tenant.api.form.category.UpdateCategoryForm;
import com.tenant.api.storage.tenant.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToCategoryDto")
    CategoryDto entityToCategoryDto(Category category);

    @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "entityToCategoryDto")
    List<CategoryDto> fromEntityToCategoryDtoList(List<Category> categories);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToCategoryAutoCompleteDto")
    CategoryDto entityToCategoryAutoCompleteDto(Category category);

    @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "entityToCategoryAutoCompleteDto")
    @Named("fromEntityToCategoryAutoCompleteDtoList")
    List<CategoryDto> fromEntityToCategoryAutoCompleteDtoList(List<Category> categories);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    Category fromCreateCategoryFormToEntity(CreateCategoryForm form);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateCategoryFormToEntity(UpdateCategoryForm form, @MappingTarget Category category);
}
