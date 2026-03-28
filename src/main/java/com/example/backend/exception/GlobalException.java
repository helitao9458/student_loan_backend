package com.example.backend.exception;


import com.example.backend.common.ResponseResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResponseResult serviceException(ServiceException e){
//        return ResponseResult.error(e.getCode(),e.getMessage());
        return new ResponseResult(e.getCode(), e.getMessage());
    }
}
