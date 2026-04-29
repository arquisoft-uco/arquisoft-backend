package com.arquisoft.seguridad.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code usuarios.usuario}.
 *
 * <p>Incluye el campo {@code keycloak_user_id} que vincula al usuario con su cuenta
 * en Keycloak (claim {@code sub} del JWT). Este campo es inmutable tras la creación.
 *
 * <p>La relación con roles se modela como {@code @ManyToMany} hacia {@link RolJpaEntity}
 * a través de la tabla de unión {@code usuarios.usuario_rol}.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "usuarios", name = "usuario")
public class UsuarioJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    /**
     * Vínculo con Keycloak — claim {@code sub} del JWT. Inmutable tras la creación.
     */
    @Column(name = "keycloak_user_id", columnDefinition = "uuid", nullable = false,
            unique = true, updatable = false)
    private UUID keycloakUserId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "identificador", nullable = false, unique = true, length = 50)
    private String identificador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoUsuarioJpaEntity estado;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = "usuarios",
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private List<RolJpaEntity> roles = new ArrayList<>();
}
