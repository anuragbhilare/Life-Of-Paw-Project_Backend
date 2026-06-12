package com.lifeofpaw.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex){
		ErrorResponse error=new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				ex.getMessage()
				);
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(jakarta.persistence.EntityNotFoundException ex){
		ErrorResponse error=new ErrorResponse(
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				ex.getMessage());
		return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex){
		if(ex.getMessage()!=null && ex.getMessage().contains("SECURITY ALERT")) {
			ErrorResponse error=new ErrorResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Internal Server Error",
					ex.getMessage());
			
			return new ResponseEntity<>(error,HttpStatus.FORBIDDEN);
		}
		
		ErrorResponse error=new ErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				ex.getMessage());
		
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(org.springframework.web.bind.MethodArgumentNotValidException ex) {
	    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
	            .map(org.springframework.validation.FieldError::getDefaultMessage)
	            .findFirst()
	            .orElse("Invalid Input Field Value passed.");

	    ErrorResponse error = new ErrorResponse(
	            HttpStatus.BAD_REQUEST.value(),
	            "Validation Failed",
	            errorMessage
	    );
	    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}



}
