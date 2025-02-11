package wooteco.subway.controller;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SubwayAdvice {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> handle(Exception e) {
        return ResponseEntity.internalServerError().body(
                Map.of("message", "[ERROR] 예상치 못한 에러가 발생했습니다. 잠시 후 다시 시도해주세요.")
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String, String>> handle(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
