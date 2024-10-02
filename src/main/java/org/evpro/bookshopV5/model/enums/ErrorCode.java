package org.evpro.bookshopV5.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {

    FB("Forbidden", HttpStatus.FORBIDDEN, ExitCode.KO),
    UA("Unauthorized", HttpStatus.UNAUTHORIZED, ExitCode.KO),
    INT("Invalid Token", HttpStatus.UNAUTHORIZED, ExitCode.KO),
    EXT("Expired Token", HttpStatus.UNAUTHORIZED, ExitCode.KO),
    ISE("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ExitCode.KO),
    IB("Invalid Body", HttpStatus.BAD_REQUEST, ExitCode.KO),
    IH("Invalid Header", HttpStatus.BAD_REQUEST, ExitCode.KO),
    BR("Bad request", HttpStatus.BAD_REQUEST, ExitCode.KO),
    RNF("The URL you have reached is not in service at this time", HttpStatus.SERVICE_UNAVAILABLE, ExitCode.KO),
    EBC("Bad Credentials", HttpStatus.BAD_REQUEST, ExitCode.KO),
    EAE("Email already exists", HttpStatus.BAD_REQUEST, ExitCode.KO),
    EUN("User not found", HttpStatus.NOT_FOUND, ExitCode.KO),

    PWM("Psw doesn't match", HttpStatus.BAD_REQUEST, ExitCode.KO),
    IVD("Invalid input data", HttpStatus.BAD_REQUEST, ExitCode.KO),
    IVP("Invalid psw", HttpStatus.BAD_REQUEST, ExitCode.KO),
    IVE("Invalid email", HttpStatus.BAD_REQUEST, ExitCode.KO),

    //User
    NCU("Any user found", HttpStatus.NO_CONTENT, ExitCode.KO),
    NU("No content in the list of user", HttpStatus.NO_CONTENT, ExitCode.KO),
    UAL("User unauthorized for this loan", HttpStatus.BAD_REQUEST, ExitCode.KO),
    URNF("Role not found", HttpStatus.NOT_FOUND, ExitCode.KO),

    //Cart
    CNF("Cart not found", HttpStatus.NOT_FOUND, ExitCode.KO),
    NC("No carts in the system", HttpStatus.NO_CONTENT, ExitCode.KO),

    //CartItem
    CINF("Cart items non found", HttpStatus.NOT_FOUND, ExitCode.KO),
    NCCI("No content in the list of cartItem", HttpStatus.NO_CONTENT, ExitCode.KO),

    //Books
    BNF("Book not found", HttpStatus.NOT_FOUND, ExitCode.KO),
    NCB("Any books found in store", HttpStatus.NO_CONTENT, ExitCode.KO),

    //Loan
    LNF("Loan not found", HttpStatus.NOT_FOUND, ExitCode.KO),
    LAR("Loan already returned", HttpStatus.BAD_REQUEST, ExitCode.KO),
    LD("Due date is already expired", HttpStatus.BAD_REQUEST, ExitCode.KO),
    NL("No content in the list of loan", HttpStatus.NO_CONTENT, ExitCode.KO);

    private String message;
    private HttpStatus status;
    private ExitCode exitCode;

}
