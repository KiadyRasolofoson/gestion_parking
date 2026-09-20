package com.example.parking.dto.request;

import com.example.parking.entity.enums.Role;
import com.example.parking.entity.enums.UserCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 60, message = "Le nom d'utilisateur doit contenir entre 3 et 60 caracteres")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 72, message = "Le mot de passe doit contenir au moins 6 caracteres")
    private String password;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 80)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 80)
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 120)
    private String email;

    @Pattern(regexp = "^$|^[0-9+ .-]{6,30}$", message = "Numero de telephone invalide")
    private String phone;

    @NotNull(message = "Le role est obligatoire")
    private Role role = Role.USER;

    @NotNull(message = "La categorie est obligatoire")
    private UserCategory category = UserCategory.STANDARD;

    private boolean enabled = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserCategory getCategory() {
        return category;
    }

    public void setCategory(UserCategory category) {
        this.category = category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
