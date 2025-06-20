package com.tenant.api.controller;

import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.person.PersonDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.person.CreatePersonForm;
import com.tenant.api.form.person.UpdatePersonForm;
import com.tenant.api.mapper.PersonMapper;
import com.tenant.api.storage.tenant.criteria.PersonCriteria;
import com.tenant.api.storage.tenant.model.Person;
import com.tenant.api.storage.tenant.repository.MoviePersonRepository;
import com.tenant.api.storage.tenant.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/person")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class PersonController extends ABasicController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private MoviePersonRepository moviePersonRepository;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PSN_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreatePersonForm form) {
        Person person = personMapper.fromCreatePersonFormToEntity(form);
        personRepository.save(person);
        return makeSuccessResponse("Create person success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PSN_V')")
    public ApiMessageDto<PersonDto> getById(@PathVariable("id") Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Person] Not found", ErrorCode.PERSON_ERROR_NOT_FOUND));

        return makeSuccessResponse(personMapper.entityToPersonDto(person), "Get person success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PSN_L')")
    public ApiMessageDto<ResponseListDto<List<PersonDto>>> list(PersonCriteria criteria, Pageable pageable) {
        Page<Person> movies = personRepository.findAll(criteria.getSpecification(), pageable);

        ResponseListDto<List<PersonDto>> responseListDto = makeResponseListDto(movies, personMapper::fromEntityToPersonDtoList);
        return makeSuccessResponse(responseListDto, "List person success");
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PSN_L')")
    public ApiMessageDto<ResponseListDto<List<PersonDto>>> autoComplete(PersonCriteria criteria, Pageable pageable) {
        Page<Person> movies = personRepository.findAll(criteria.getSpecification(), pageable);

        ResponseListDto<List<PersonDto>> responseListDto = makeResponseListDto(movies, personMapper::fromEntityToPersonAutoCompleteDtoList);
        return makeSuccessResponse(responseListDto, "List auto complete person success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PSN_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdatePersonForm form) {
        Person person = personRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Person] Not found", ErrorCode.PERSON_ERROR_NOT_FOUND));

        person.getKinds().clear();
        personMapper.fromUpdatePersonFormToEntity(form, person);

        personRepository.save(person);
        return makeSuccessResponse("Update person success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PSN_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Person] Not found", ErrorCode.PERSON_ERROR_NOT_FOUND));
        if (moviePersonRepository.existsByPersonId(person.getId())) {
            throw new BadRequestException("[Person] Cannot delete with relationship with Movie Person", ErrorCode.PERSON_ERROR_MOVIE_PERSON_EXISTED);
        }
        person.getKinds().clear();
        personRepository.delete(person);
        return makeSuccessResponse("Delete person success");
    }
}
