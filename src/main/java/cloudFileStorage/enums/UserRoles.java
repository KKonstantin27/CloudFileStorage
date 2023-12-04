package cloudFileStorage.enums;

public enum UserRoles {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
    private String userRole;
    UserRoles(String userRole) {
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }
}
