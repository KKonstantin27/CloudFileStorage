package cloudFileStorage.dto;

import cloudFileStorage.enums.UserObjectSizeUnits;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserObjectDTO {
    private String name;
    private String size;
    private String userStorageName;
    private String path;
    boolean isDir;

    public UserObjectDTO(String name, String size, String userStorageName, String path, boolean isDir) {
        this.name = name;
        this.size = size;
        this.userStorageName = userStorageName;
        this.path = path;
        this.isDir = isDir;
    }
}


