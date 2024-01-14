package cloudFileStorage.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserObjectDTO {
    private String name;
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 1, max = 30, message = "Name should be between 1 and 20 characters")
    @Pattern(regexp = "[^\\\\/:*?\"<>|+\\x00-\\x1F\\x20.]+", message = "Name contains invalid characters")
    private String shortName;
    private String size;
    private String userStorageName;
    private String path;
    boolean isDir;

    public UserObjectDTO(String name, String shortName, String size, String userStorageName, String path, boolean isDir) {
        this.name = name;
        this.shortName = shortName;
        this.size = size;
        this.userStorageName = userStorageName;
        this.path = path;
        this.isDir = isDir;
    }
}


