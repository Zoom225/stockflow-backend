package com.stockflow.exception;

import com.stockflow.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Map<HttpStatus, String> STATUS_LABELS = new EnumMap<>(HttpStatus.class);

	static {
		STATUS_LABELS.put(HttpStatus.BAD_REQUEST, "Requete incorrecte");
		STATUS_LABELS.put(HttpStatus.NOT_FOUND, "Ressource introuvable");
		STATUS_LABELS.put(HttpStatus.CONFLICT, "Conflit");
		STATUS_LABELS.put(HttpStatus.UNAUTHORIZED, "Non autorise");
		STATUS_LABELS.put(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
			ResourceNotFoundException exception,
			HttpServletRequest request
	) {
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateResource(
			DuplicateResourceException exception,
			HttpServletRequest request
	) {
		return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(ProductDeletionNotAllowedException.class)
	public ResponseEntity<ApiErrorResponse> handleProductDeletionNotAllowed(
			ProductDeletionNotAllowedException exception,
			HttpServletRequest request
	) {
		return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(AuthenticationFailedException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthenticationFailed(
			AuthenticationFailedException exception,
			HttpServletRequest request
	) {
		return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ApiErrorResponse> handleInsufficientStock(
			InsufficientStockException exception,
			HttpServletRequest request
	) {
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(
			MethodArgumentNotValidException exception,
			HttpServletRequest request
	) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return buildResponse(
				HttpStatus.BAD_REQUEST,
				"La validation a echoue",
				request.getRequestURI(),
				fieldErrors
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(
			Exception exception,
			HttpServletRequest request
	) {
		return buildResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Une erreur inattendue est survenue",
				request.getRequestURI(),
				null
		);
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(
			HttpStatus status,
			String message,
			String path,
			Map<String, String> fieldErrors
	) {
		ApiErrorResponse response = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				STATUS_LABELS.getOrDefault(status, status.getReasonPhrase()),
				message,
				path,
				fieldErrors
		);

		return ResponseEntity.status(status).body(response);
	}
}
