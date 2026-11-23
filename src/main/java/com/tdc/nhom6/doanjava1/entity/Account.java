package com.tdc.nhom6.doanjava1.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    @Column(nullable = false, length = 100)
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String role = "STUDENT"; // ADMIN, LIBRARIAN, STUDENT

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Account() {}

    // ── Convenience helpers ────────────────────────────────────────────
    public boolean isAdmin()     { return "ADMIN".equals(role); }
    public boolean isLibrarian() { return "LIBRARIAN".equals(role); }
    public boolean canManage()   { return isAdmin() || isLibrarian(); }

    // ── Getters & Setters ─────────────────────────────────────────────
    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }
    public String getUsername()               { return username; }
    public void setUsername(String u)         { this.username = u; }
    public String getPassword()               { return password; }
    public void setPassword(String p)         { this.password = p; }
    public String getFullName()               { return fullName; }
    public void setFullName(String f)         { this.fullName = f; }
    public String getEmail()                  { return email; }
    public void setEmail(String e)            { this.email = e; }
    public String getRole()                   { return role; }
    public void setRole(String r)             { this.role = r; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
    public boolean isActive()                 { return active; }
    public void setActive(boolean a)          { this.active = a; }
}
