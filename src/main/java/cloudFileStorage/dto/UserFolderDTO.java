package cloudFileStorage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserFolderDTO extends UserObjectDTO {

    public UserFolderDTO(String name, String shortName, String userStorageName, String path) {
        super(name, shortName, "-", userStorageName, path, true);
    }

}
