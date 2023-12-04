package cloudFileStorage.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
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

    @Column(name = "role")
    private String role;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
