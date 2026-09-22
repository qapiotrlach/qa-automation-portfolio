package io.github.qapiotrlach.playground.error;

import io.github.qapiotrlach.playground.product.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;

/**
 * Zamienia wyjatki na odpowiedzi zgodne z RFC 9457 (Problem Details for HTTP APIs).
 * Dzieki temu klient - w tym testy automatyczne - dostaje zawsze ten sam ksztalt bledu.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record FieldViolation(String field, String message) {}

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFound(ProductNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Produkt nie znaleziony");
        problem.setType(URI.create("https://qa-playground/errors/product-not-found"));
        problem.setProperty("productId", exception.getProductId());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        List<FieldViolation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Zadanie zawiera nieprawidlowe dane");
        problem.setTitle("Blad walidacji");
        problem.setType(URI.create("https://qa-playground/errors/validation"));
        problem.setProperty("violations", violations);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
