package com.arquisoft.seguridad.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code usuarios.rol}.
 *
 * <p>Representa el catálogo inmutable de roles del sistema. El campo {@code nombre}
 * coincide exactamente con el código de {@link com.arquisoft.seguridad.domain.model.UsuarioRole}
 * (ej. {@code "ESTUDIANTE"}, {@code "ADMINISTRADOR"}).
 *
 * <p>Los roles son datos de referencia pre-poblados por la migración Flyway {@code V1}.
 * No se crean ni eliminan por HUs de negocio.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "usuarios", name = "rol")
public class RolJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    /**
     * Código del rol — coincide exactamente con {@code UsuarioRole.getCode()}.
     * Ej: {@code "ESTUDIANTE"}, {@code "COORDINADOR"}, {@code "ADMINISTRADOR"}.
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;
}
