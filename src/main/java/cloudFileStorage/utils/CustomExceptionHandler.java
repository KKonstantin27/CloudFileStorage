package cloudFileStorage.utils;

import io.minio.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final String DEFAULT_MESSAGE_FOR_500 = "Internal Server Error. Try again later or contact your administrator";

    @ExceptionHandler({ServerException.class, InsufficientDataException.class, ErrorResponseException.class, IOException.class,
            NoSuchAlgorithmException.class, InvalidKeyException.class, InvalidResponseException.class, XmlParserException.class,
            InternalException.class})

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(Model model) {
        ModelAndView view = new ModelAndView();
        model.addAttribute("errorMessage", DEFAULT_MESSAGE_FOR_500);
        model.addAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        view.setViewName("errorPage");
        return view;
    }
}
