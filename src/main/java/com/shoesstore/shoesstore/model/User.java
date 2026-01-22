package com.shoesstore.shoesstore.model;

import com.shoesstore.shoesstore.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre {min} y {max} caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener al menos {min} caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 100, message = "El nombre completo no puede superar los {max} caracteres")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede superar los {max} caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(
            mappedBy = "user",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
            orphanRemoval = false
    )
    private List<PurchaseOrder> ordenes = new ArrayList<>();

    //Deberia ir protected pero para mantener el funcionamiento en el codigo actual
    //es importante
    public User() {
    	
    }

	//esto deberia ser privado
    public User(String username, String password, String fullName, String email, Role role) {
    	this.username = username;
    	this.password = password;
    	this.fullName = fullName;
    	this.email = email; 
    	this.role = role;
    	this.createdAt = LocalDateTime.now();
    }
    
    public static User create(String username, String encodedPassword, String fullName, String email, Role role) {
    	return new User(username, encodedPassword, fullName, email, role);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<PurchaseOrder> getOrdenes() {
		return ordenes;
	}

	public void setOrdenes(List<PurchaseOrder> ordenes) {
		this.ordenes = ordenes;
	}

}