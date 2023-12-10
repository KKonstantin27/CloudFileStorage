package cloudFileStorage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserFolderDTO extends UserObjectDTO {

    public UserFolderDTO (String name, long size, String userStorageName, String path) {
        super(name, "-", userStorageName, path, true);
    }

}
