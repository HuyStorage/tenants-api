package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.favourite.FavouriteDto;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.form.favourite.CreateFavouriteForm;
import com.tenant.api.mapper.FavouriteMapper;
import com.tenant.api.storage.tenant.criteria.FavouriteCriteria;
import com.tenant.api.storage.tenant.model.Favourite;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.Person;
import com.tenant.api.storage.tenant.model.User;
import com.tenant.api.storage.tenant.repository.FavouriteRepository;
import com.tenant.api.storage.tenant.repository.MovieRepository;
import com.tenant.api.storage.tenant.repository.PersonRepository;
import com.tenant.api.storage.tenant.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/favourite")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class FavouriteController extends ABasicController {

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private FavouriteMapper favouriteMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Long> create(@Valid @RequestBody CreateFavouriteForm form) {
        User user = userRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[User] user not found"));

        Favourite favourite = favouriteRepository.findByUserIdAndTypeAndTargetId(user.getId(), form.getType(), form.getTargetId())
                .orElse(null);
        if (favourite != null) {
            return makeSuccessResponse(favourite.getId(), "Create favourite success");
        }

        favourite = new Favourite();
        favourite.setUser(user);
        favourite.setType(form.getType());

        if (Objects.equals(form.getType(), BaseConstant.FAVOURITE_TYPE_MOVIE)) {
            Movie movie = movieRepository.findById(form.getTargetId())
                    .orElseThrow(() -> new NotFoundException("[Movie] not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));
            favourite.setMovie(movie);
        } else {
            Person person = personRepository.findById(form.getTargetId())
                    .orElseThrow(() -> new NotFoundException("[Person] not found", ErrorCode.PERSON_ERROR_NOT_FOUND));
            favourite.setPerson(person);
        }

        favourite = favouriteRepository.save(favourite);
        return makeSuccessResponse(favourite.getId(), "Create favourite success");
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<FavouriteDto> get(@Param("targetId") Long targetId, @Param("targetId") Integer type) {
        Favourite favourite = favouriteRepository.findByUserIdAndTypeAndTargetId(getCurrentUser(), type, targetId)
                .orElseThrow(() -> new NotFoundException("[Favourite] not found", ErrorCode.FAVOURITE_ERROR_NOT_FOUND));
        FavouriteDto favouriteDto = new FavouriteDto();
        favouriteDto.setId(favourite.getId());
        return makeSuccessResponse(favouriteDto, "Get favourite success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<FavouriteDto>>> list(FavouriteCriteria criteria, Pageable pageable) {
        criteria.setUserId(getCurrentUser());
        Page<Favourite> favourites = favouriteRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(favourites, favouriteMapper::fromEntityToFavouriteDtoList), "List favourite success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Favourite favourite = favouriteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Favourite] Not found", ErrorCode.FAVOURITE_ERROR_NOT_FOUND));
        if (!favourite.getUser().getId().equals(getCurrentUser())) {
            throw new UnauthorizationException("Not allow");
        }
        favouriteRepository.delete(favourite);
        return makeSuccessResponse("Delete favourite success");
    }
}
