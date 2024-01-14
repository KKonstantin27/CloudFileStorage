package cloudFileStorage.dto;

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
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 4, max = 20, message = "Username should be between 8 and 20 characters")
    @Pattern(regexp = "^(?!.*\\s)[a-zA-Z\\d._-]*$", message = "Username contains invalid characters")
    private String username;

    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters")
    @Pattern(regexp = "^(?!.*\\s)[a-zA-Z\\d!@#$%^&*()_=+;:?.,<>]*$", message = "Password contains invalid characters")
    private String password;

    private String repeatPassword;
}
