package com.tenant.api.exception;


import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.form.ErrorForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    final ObjectMapper mapper = new ObjectMapper();
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiMessageDto<String>> globleExcpetionHandler(NotFoundException ex) {
        log.error(""+ex.getMessage(), ex);
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setCode("ERROR");
        apiMessageDto.setResult(false);
        apiMessageDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessageDto, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setCode("ERROR handleNoHandlerFoundException");
        apiMessageDto.setResult(false);
        apiMessageDto.setMessage("[Ex3]: 404");
        return new ResponseEntity<>(apiMessageDto, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiMessageDto<List<ErrorForm>> exceptionHandler(Exception ex) {
        log.error(""+ex.getMessage(), ex);
        ApiMessageDto<List<ErrorForm>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setCode("ERROR");
        apiMessageDto.setResult(false);
        if(ex instanceof MyBindingException){
            try {
                List<ErrorForm> errorForms = Arrays.asList(mapper.readValue(ex.getMessage(), ErrorForm[].class));
                apiMessageDto.setData(errorForms);
                apiMessageDto.setMessage("Invalid form");
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }else{
            apiMessageDto.setMessage("[Ex2]: "+ex.getMessage());
        }
        return apiMessageDto;
    }

    @ExceptionHandler({UnauthorizationException.class})
    public ResponseEntity<ApiMessageDto<String>> notAllow(UnauthorizationException ex) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setResult(false);
        apiMessageDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessageDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ApiMessageDto<String>> badRequest(BadRequestException ex) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setResult(false);
        apiMessageDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(apiMessageDto, HttpStatus.BAD_REQUEST);
    }
}
