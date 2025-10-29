package com.tenant.api.mapper;

import com.tenant.api.dto.review.ReviewDto;
import com.tenant.api.form.comment.UpdateCommentForm;
import com.tenant.api.form.review.CreateReviewForm;
import com.tenant.api.form.review.UpdateReviewForm;
import com.tenant.api.storage.tenant.model.Comment;
import com.tenant.api.storage.tenant.model.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class})
public interface ReviewMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "author", target = "author", qualifiedByName = "entityToUserDto")
    @Mapping(source = "movieId", target = "movieId")
    @Mapping(source = "rate", target = "rate")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "totalLike", target = "totalLike")
    @Mapping(source = "totalDislike", target = "totalDislike")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToReviewDto")
    ReviewDto entityToReviewDto(Review review);

    @IterableMapping(elementTargetType = ReviewDto.class, qualifiedByName = "entityToReviewDto")
    List<ReviewDto> fromEntityToReviewDtoList(List<Review> reviews);

    @Mapping(source = "rate", target = "rate")
    @Mapping(source = "content", target = "content")
    @BeanMapping(ignoreByDefault = true)
    Review fromCreateReviewFormToEntity(CreateReviewForm form);

    @Mapping(source = "rate", target = "rate")
    @Mapping(source = "content", target = "content")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateReviewFormToEntity(UpdateReviewForm form, @MappingTarget Review review);
}
