package cloudFileStorage.enums;

public enum UserRoles {
    USER("USER"),
    ADMIN("ADMIN");
    private String userRole;
    UserRoles(String userRole) {
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }
}
