package edu.wayne.capstone.references.data;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import edu.wayne.capstone.references.config.BadArticleVersionException;
import edu.wayne.capstone.references.config.BadPubmedIdException;


@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
	
    @ExceptionHandler(value = { ConversionFailedException.class, BadArticleVersionException.class, BadPubmedIdException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = String.format("Invalid value provided. %s", ex.getMessage());
        HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex, bodyOfResponse, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }
}
