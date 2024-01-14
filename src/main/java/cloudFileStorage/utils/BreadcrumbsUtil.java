package cloudFileStorage.utils;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BreadcrumbsUtil {
    private final static String FOLDER_DELIMITER = "/";

    public Map<String, String> buildBreadcrumbs(String userStorageName, String path) {
        Map<String, String> breadcrumbs = new LinkedHashMap<>();
        breadcrumbs.put("", userStorageName);

        if (path == null) {
            return breadcrumbs;
        }

        String[] pathArr = path.split(FOLDER_DELIMITER);
        StringBuilder currentPath = new StringBuilder();
        for (String fragmentOfPath : pathArr) {
            breadcrumbs.put((currentPath
                            .append(fragmentOfPath)
                            .append(FOLDER_DELIMITER))
                            .toString(),
                    fragmentOfPath + FOLDER_DELIMITER);
        }
        return breadcrumbs;
    }
}
