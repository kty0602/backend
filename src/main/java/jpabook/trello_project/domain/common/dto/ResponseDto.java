package jpabook.trello_project.domain.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseDto<T>{
    public int statusCode;
    public String message;
    public T data;


    // 사용 예시
//    @GetMapping("/{storeId}")
//    public ResponseEntity<ResponseDto<GetStoreResponseDto>> getStore(@PathVariable("storeId") Long storeId) {
//        GetStoreResponseDto responseDto = storeService.getStore(storeId);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ResponseDto.of(200, "성공적으로 조회되었습니다.", responseDto));
//    }


    public static <T> ResponseDto<T> of(int statusCode, String message, T data) {
        return new ResponseDto<T>(statusCode, message, data);
    }

    public static <T> ResponseDto<T> of(int statusCode, T data) {
        return new ResponseDto<T>(statusCode, "", data);
    }

    public static <T> ResponseDto<T> of(int statusCode, String message) {
        return new ResponseDto<T>(statusCode, message, null);
    }

    public static <T> ResponseDto<T> of(int statusCode) {
        return new ResponseDto<T>(statusCode, "", null);
    }

    public static <T> ResponseDto<T> of(HttpStatus statusCode, String message, T data) {
        return new ResponseDto<T>(statusCode.value(), message, data);
    }

    public static <T> ResponseDto<T> of(HttpStatus statusCode, T data) {
        return new ResponseDto<T>(statusCode.value(), "", data);
    }

    public static <T> ResponseDto<T> of(HttpStatus statusCode, String message) {
        return new ResponseDto<T>(statusCode.value(), message, null);
    }

    public static <T> ResponseDto<T> of(HttpStatus statusCode) {
        return new ResponseDto<T>(statusCode.value(), "", null);
    }
}