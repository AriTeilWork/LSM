public abstract class User {
    private int id;
    private String username;
    private String password;
    private Role role;
    private String readerId;
    private String email;
    private String createdAt;

    public User(int id, String username, String password, Role role, String readerId, String email, String createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.readerId = readerId;
        this.email = email;
        this.createdAt = createdAt;
    }

    public User(String username, String password, Role role, String readerId) {
        this(0, username, password, role, readerId, null, null);
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

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getReaderId() {
        return readerId;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN || role == Role.SUPER_ADMIN;
    }

    public boolean isSuperAdmin() {
        return role == Role.SUPER_ADMIN;
    }

    public boolean isUser() {
        return role == Role.USER;
    }

    public abstract void borrowItem(LibraryItem item);

    public abstract void returnItem(LibraryItem item);
}
