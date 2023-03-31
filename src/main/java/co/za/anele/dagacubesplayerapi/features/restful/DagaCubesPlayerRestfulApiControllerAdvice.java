package co.za.anele.dagacubesplayerapi.features.restful;

import co.za.anele.dagacubesplayerapi.features.data.ProblemDTO;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidPlayerIdException;
import co.za.anele.dagacubesplayerapi.features.exceptions.InvalidTransactionAmount;
import co.za.anele.dagacubesplayerapi.features.exceptions.WagerGreaterThanBalanceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DagaCubesPlayerRestfulApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {InvalidPlayerIdException.class})
    ResponseEntity<Object> handleInvalidPlayerIdException(InvalidPlayerIdException exception, WebRequest request) {
        ProblemDTO problem = new ProblemDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage());

        return handleExceptionInternal(exception, problem, new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {InvalidTransactionAmount.class})
    ResponseEntity<Object> handleInvalidTransactionAmount(InvalidTransactionAmount exception, WebRequest request) {
        ProblemDTO problem = new ProblemDTO(HttpStatus.BAD_REQUEST.value(), exception.getMessage());

        return handleExceptionInternal(exception, problem, new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {WagerGreaterThanBalanceException.class})
    ResponseEntity<Object> handleWagerGreaterThanBalanceException(WagerGreaterThanBalanceException exception, WebRequest request) {
        ProblemDTO problem = new ProblemDTO(HttpStatus.I_AM_A_TEAPOT.value(), exception.getMessage());

        return handleExceptionInternal(exception, problem, new HttpHeaders(),
                HttpStatus.I_AM_A_TEAPOT, request);
    }
}
