package com.example.littleProject.controller.dto.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler {
    @Autowired
    StatusResponse statusResponse;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        statusResponse.setResultList(new ArrayList<>());
        statusResponse.setResponseCode("002"); //002 - 參數檢核錯誤
        String message1;
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        message1 = allErrors.stream().map(n -> String.valueOf(n.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        System.out.println(message1); //print不出
        statusResponse.setMessage(message1);

        return new ResponseEntity<Object>(statusResponse, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        statusResponse.setResultList(new ArrayList<>());
        statusResponse.setResponseCode("002");
        statusResponse.setMessage("輸入格式錯誤");
        return new ResponseEntity<Object>(statusResponse, HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        statusResponse.setResultList(new ArrayList<>());
        statusResponse.setResponseCode("005");
        statusResponse.setMessage("伺服器忙碌中，請稍後嘗試");
        return new ResponseEntity<Object>(statusResponse, HttpStatus.OK);
    }
}
