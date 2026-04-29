package com.arquisoft.seguridad.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla catálogo {@code usuarios.estado_usuario}.
 *
 * <p>Esta tabla normaliza los estados posibles de un usuario, reemplazando
 * la columna VARCHAR con CHECK constraint que existía en {@code usuarios.usuario}.
 *
 * <p>La columna {@code nombre} coincide con el código del enum
 * {@link com.arquisoft.seguridad.domain.model.EstadoUsuario}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "usuarios", name = "estado_usuario")
public class EstadoUsuarioJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    /**
     * Código del estado — coincide con {@code EstadoUsuario.getCode()}.
     * Ejemplo: {@code "ACTIVO"}, {@code "INACTIVO"}.
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 20)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;
}
