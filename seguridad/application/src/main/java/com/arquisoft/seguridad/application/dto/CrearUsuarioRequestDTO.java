package com.arquisoft.seguridad.application.dto;

import com.arquisoft.seguridad.domain.model.UsuarioRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de comando para crear un usuario.
 * La validación de formato (email, notBlank) se hace en esta capa.
 * Las invariantes de negocio (unicidad, etc.) se validan en el aggregate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearUsuarioRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener formato válido")
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private UsuarioRole rol;
}
