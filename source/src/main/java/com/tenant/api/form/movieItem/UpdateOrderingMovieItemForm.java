package com.tenant.api.form.movieItem;

import com.tenant.api.validation.MovieItemKindConstraint;
import com.tenant.api.validation.OrderingListConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@ApiModel
@OrderingListConstraint
public class UpdateOrderingMovieItemForm {

    private List<@Valid OrderingMovieItemForm> orderingMovieItems;

    @MovieItemKindConstraint
    @ApiModelProperty(required = true)
    private Integer kind;
}
