package com.javier.parking.dto;

import com.javier.parking.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "Username no puede estar vacio")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Password no puede estar vacio")
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 20 caracteres")
    @Pattern(regexp = ".*\\d.*", message = "La contraseña debe contener al menos un numero")
    private String password;

    private Role role;
}