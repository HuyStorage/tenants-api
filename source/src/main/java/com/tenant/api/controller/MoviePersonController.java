package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.moviePerson.MoviePersonDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.moviePerson.AddMoviePersonForm;
import com.tenant.api.form.moviePerson.AddMoviePersonItemForm;
import com.tenant.api.form.moviePerson.OrderingMoviePersonForm;
import com.tenant.api.form.moviePerson.UpdateMoviePersonForm;
import com.tenant.api.mapper.MoviePersonMapper;
import com.tenant.api.storage.tenant.criteria.MoviePersonCriteria;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.MoviePerson;
import com.tenant.api.storage.tenant.model.Person;
import com.tenant.api.storage.tenant.repository.MoviePersonRepository;
import com.tenant.api.storage.tenant.repository.MovieRepository;
import com.tenant.api.storage.tenant.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/movie-person")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class MoviePersonController extends ABasicController {

    @Autowired
    private MoviePersonRepository moviePersonRepository;

    @Autowired
    private MoviePersonMapper moviePersonMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PersonRepository personRepository;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_P_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody AddMoviePersonForm form) {
        Movie movie = movieRepository.findById(form.getMovieId())
                .orElseThrow(() -> new NotFoundException("[Movie] not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));
        List<Long> personIds = form.getPersons().stream()
                .map(AddMoviePersonItemForm::getPersonId)
                .collect(Collectors.toList()); // personIds incoming

        List<Person> personList = personRepository.findAllById(personIds);
        if (personList.size() != personIds.size()) {
            throw new NotFoundException("[Person] not found", ErrorCode.PERSON_ERROR_NOT_FOUND);
        }
        Map<Long, Person> personMap = personList.stream()
                .collect(Collectors.toMap(Person::getId, p -> p)); // key: personId, value: Person

        // delete not in persons incoming
        moviePersonRepository.deleteByMovieIdAndPersonIdNotIn(movie.getId(), personIds);

        List<MoviePerson> moviePersonOld = moviePersonRepository.findAllByMovieId(movie.getId());
        Map<Long, MoviePerson> moviePersonMap = moviePersonOld.stream()
                .collect(Collectors.toMap(mp -> mp.getPerson().getId(), mp -> mp)); // key: personId, value: MoviePerson

        List<MoviePerson> moviePersonList = new ArrayList<>();
        for (AddMoviePersonItemForm item : form.getPersons()) {
            Person person = personMap.get(item.getPersonId());
            MoviePerson moviePerson;

            // if existed -> update
            if (moviePersonMap.containsKey(item.getPersonId())) {
                moviePerson = moviePersonMap.get(item.getPersonId());
            } else { // create new
                moviePerson = new MoviePerson();
                moviePerson.setMovie(movie);
                moviePerson.setPerson(person);
                moviePerson.setKind(item.getKind());
            }
            moviePerson.setOrdering(item.getOrdering());
            moviePerson.setCharacterName(item.getCharacterName() != null && item.getKind().equals(BaseConstant.PERSON_KIND_ACTOR)
                    ? item.getCharacterName() : null);

            moviePersonList.add(moviePerson);
        }
        moviePersonRepository.saveAll(moviePersonList);
        return makeSuccessResponse("Create movie persons success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_P_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateMoviePersonForm form) {
        MoviePerson moviePerson = moviePersonRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Movie Person] Not found", ErrorCode.MOVIE_PERSON_ERROR_NOT_FOUND));

        if (form.getKind().equals(BaseConstant.PERSON_KIND_DIRECTOR) && form.getCharacterName() != null) {
            throw new BadRequestException("[Movie Person] Kind invalid", ErrorCode.MOVIE_PERSON_ERROR_KIND_INVALID);
        }
        moviePerson.setKind(form.getKind());
        moviePerson.setCharacterName(form.getCharacterName());
        moviePersonRepository.save(moviePerson);
        return makeSuccessResponse("Update movie persons success");
    }

    @PutMapping(value = "/update-ordering", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_P_U')")
    public ApiMessageDto<Void> updateOrdering(@RequestBody List<@Valid OrderingMoviePersonForm> form) {
        if (form == null || form.isEmpty()) {
            throw new BadRequestException("Input list cannot be empty", ErrorCode.MOVIE_PERSON_ERROR_INVALID_REQUEST);
        }
        List<Long> ids = form.stream()
                .map(OrderingMoviePersonForm::getId)
                .collect(Collectors.toList());
        List<MoviePerson> moviePersonList = moviePersonRepository.findAllById(ids);

        if (moviePersonList.size() != ids.size()) {
            throw new NotFoundException("[Movie Person] Not found", ErrorCode.MOVIE_PERSON_ERROR_NOT_FOUND);
        }

        Map<Long, Integer> orderingMap = form.stream()
                .collect(Collectors.toMap(OrderingMoviePersonForm::getId, OrderingMoviePersonForm::getOrdering));

        for (MoviePerson item : moviePersonList) {
            item.setOrdering(orderingMap.get(item.getId()));
        }
        moviePersonRepository.saveAll(moviePersonList);

        return makeSuccessResponse("Update movie person success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_P_L')")
    public ApiMessageDto<ResponseListDto<List<MoviePersonDto>>> list(MoviePersonCriteria criteria, Pageable pageable) {
        Page<MoviePerson> moviePersonPage = moviePersonRepository.findAll(criteria.getSpecification(), pageable);

        ResponseListDto<List<MoviePersonDto>> responseListDto = makeResponseListDto(moviePersonPage, moviePersonMapper::fromEntityToMoviePersonDtoList);
        return makeSuccessResponse(responseListDto, "List person success");
    }
}
