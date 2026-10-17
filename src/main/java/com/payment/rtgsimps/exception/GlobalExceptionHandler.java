package com.payment.rtgsimps.exception;
import com.payment.rtgsimps.api.ApiError;import com.payment.rtgsimps.service.*;import jakarta.validation.ConstraintViolationException;import org.springframework.http.*;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.MissingRequestHeaderException;import org.springframework.web.bind.annotation.*;import java.time.Instant;import java.util.*;import java.util.stream.Collectors;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(PaymentNotFoundException.class) ResponseEntity<ApiError> notFound(){return err(HttpStatus.NOT_FOUND,"Payment not found",Map.of());}
 @ExceptionHandler(IdempotencyConflictException.class) ResponseEntity<ApiError> conflict(){return err(HttpStatus.CONFLICT,"Idempotency key conflicts with existing request",Map.of());}
 @ExceptionHandler({IllegalArgumentException.class,ConstraintViolationException.class}) ResponseEntity<ApiError> bad(Exception e){return err(HttpStatus.BAD_REQUEST,"Invalid request",Map.of());}
 @ExceptionHandler(MissingRequestHeaderException.class) ResponseEntity<ApiError> missing(MissingRequestHeaderException e){return err(HttpStatus.BAD_REQUEST,"Missing header: "+e.getHeaderName(),Map.of());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException e){Map<String,String> m=e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(x->x.getField(),x->Objects.toString(x.getDefaultMessage(),"Invalid value"),(a,b)->a));return err(HttpStatus.BAD_REQUEST,"Validation failed",m);}
 @ExceptionHandler(IllegalStateException.class) ResponseEntity<ApiError> state(IllegalStateException e){return err(HttpStatus.CONFLICT,"Request cannot be processed in current state",Map.of());}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiError> internal(Exception e){return err(HttpStatus.INTERNAL_SERVER_ERROR,"Internal server error",Map.of());}
 private ResponseEntity<ApiError> err(HttpStatus s,String m,Map<String,String> v){return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),s.getReasonPhrase(),m,v));}
}
