package cloudFileStorage.dto;

import cloudFileStorage.enums.UserObjectSizeUnits;
import lombok.Data;

@Data
public class UserObjectDTO {
    private String name;
    private String size;
    boolean isDir;

    public UserObjectDTO(String name, long size, boolean isDir) {
        this.name = name;
        this.isDir = isDir;
        if (isDir) {
            this.size = "-";
        } else {
            this.size = UserObjectSizeUnits.convertSizeToRequiredUnit(size);
        }
    }
}


