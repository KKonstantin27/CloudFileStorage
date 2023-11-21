package cloudFileStorage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", unique = true, nullable = false)
//    @NotEmpty(message = "Имя пользователя не может быть пустым")
//    @Size(min = 4, max = 20, message = "Длина имени пользователя должна быть от 4 до 20 символов")
    private String username;

    @Column(name = "password", nullable = false)
//    @Size(min = 8, max = 20, message = "Длина пароля должна быть от 8 до 20 символов")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
