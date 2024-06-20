package com.mukund.booknetwork.role.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;


//@Setter
//@NoArgsConstructor

public enum BusinessErrorCodes {
 NO_CODE(0,HttpStatus.NOT_IMPLEMENTED,"NO CODE"),
    INCORRECT_PASSWORD(300,HttpStatus.BAD_REQUEST,"INCORRECT PASSWORD"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST,"NEW PASSWORD DOES NOT MATCH"),
 ACCOUNT_LOCKED(302,HttpStatus.FORBIDDEN,"USER ACCOUNT IS LOCKED"),
 ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN,"USER ACCOUNT IS DISABLED"),
 BAD_CREDENTIALS(304,HttpStatus.FORBIDDEN,"LOGIN CREDENTIALS ARE INCORRECT"),
    Duplicate_Email(305,HttpStatus.BAD_REQUEST," Account with this Email Already Exist")
    ;

 @Getter
    private final int code;
    @Getter
    private  final String description;
    @Getter
    private final  HttpStatus httpStatus;

    BusinessErrorCodes(int code,  HttpStatus httpStatus,String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
