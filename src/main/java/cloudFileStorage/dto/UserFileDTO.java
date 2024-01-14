package cloudFileStorage.dto;

import cloudFileStorage.enums.UserObjectSizeUnits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserFileDTO extends UserObjectDTO {

    public UserFileDTO(String name, String shortName, long size, String userStorageName, String path) {
        super(name, shortName, UserObjectSizeUnits.convertSizeToRequiredUnit(size), userStorageName, path, false);
    }
}
