package cloudFileStorage.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    @NotEmpty(message = "Имя пользователя не может быть пустым")
    @Size(min = 4, max = 20, message = "Длина имени пользователя должна быть от 4 до 20 символов")
    @Pattern(regexp = "^(?!.*\\s)[a-zA-Z\\d._-]*$", message = "Имя пользователя содержит недопустимые символы")
    private String username;

    @Size(min = 8, max = 20, message = "Длина пароля должна быть от 8 до 20 символов")
    @Pattern(regexp = "^(?!.*\\s)[a-zA-Z\\d!@#$%^&*()_=+;:?.,<>]*$", message = "Пароль содержит недопустимые символы")
    private String password;

    private String repeatPassword;

}
