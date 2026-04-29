package com.arquisoft.seguridad.infrastructure.adapter.out.persistence;

import com.arquisoft.seguridad.domain.model.EstadoUsuario;
import com.arquisoft.seguridad.domain.model.Usuario;
import com.arquisoft.seguridad.domain.model.UsuarioRole;
import com.arquisoft.seguridad.domain.port.out.UsuarioRepositoryPort;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de repositorio que implementa {@link UsuarioRepositoryPort}.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Construir {@link Specification} dinámica combinando predicados opcionales.</li>
 *   <li>Traducir {@link UsuarioJpaEntity} → {@link Usuario} de dominio usando
 *       {@code rebuild(...)} — nunca {@code build(...)}.</li>
 *   <li>Convertir {@code RolJpaEntity.nombre} → {@link UsuarioRole#fromCode(String)}
 *       al reconstruir los roles del usuario.</li>
 * </ul>
 *
 * <p>{@code @Transactional(readOnly = true)} vive aquí (infrastructure), no en el use case.
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Construye una {@link Specification} dinámica con los predicados opcionales:
     * <ul>
     *   <li>{@code nombreOEmail}: {@code LOWER(nombre) LIKE %valor%
     *       OR LOWER(apellido) LIKE %valor% OR LOWER(email) LIKE %valor%}</li>
     *   <li>{@code estado}: {@code estado = :estado}</li>
     *   <li>{@code rol}: JOIN con {@code usuario_rol} y {@code rol}
     *       filtrando {@code rol.nombre = :rolCode}</li>
     * </ul>
     */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarConFiltros(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol,
            int pagina,
            int tamano) {
        Specification<UsuarioJpaEntity> spec = construirSpecification(nombreOEmail, estado, rol);
        Page<UsuarioJpaEntity> page = jpaRepository.findAll(spec, PageRequest.of(pagina, tamano));
        return page.getContent()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Usa la misma {@link Specification} que {@link #buscarConFiltros} pero ejecuta
     * {@code count(spec)} para obtener el total sin cargar los registros.
     */
    @Override
    @Transactional(readOnly = true)
    public long contarConFiltros(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol) {
        Specification<UsuarioJpaEntity> spec = construirSpecification(nombreOEmail, estado, rol);
        return jpaRepository.count(spec);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos privados
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Construye la {@link Specification} dinámica combinando los predicados opcionales.
     * Solo se agrega un predicado si el parámetro correspondiente no es nulo.
     */
    private Specification<UsuarioJpaEntity> construirSpecification(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();

            if (nombreOEmail != null && !nombreOEmail.isBlank()) {
                String patron = "%" + nombreOEmail.toLowerCase() + "%";
                Predicate porNombre   = cb.like(cb.lower(root.get("nombre")),   patron);
                Predicate porApellido = cb.like(cb.lower(root.get("apellido")), patron);
                Predicate porEmail    = cb.like(cb.lower(root.get("email")),    patron);
                predicados.add(cb.or(porNombre, porApellido, porEmail));
            }

            if (estado != null) {
                Join<UsuarioJpaEntity, EstadoUsuarioJpaEntity> joinEstado =
                        root.join("estado", JoinType.INNER);
                predicados.add(cb.equal(joinEstado.get("nombre"), estado.getCode()));
            }

            if (rol != null) {
                Join<UsuarioJpaEntity, RolJpaEntity> joinRoles =
                        root.join("roles", JoinType.INNER);
                predicados.add(cb.equal(joinRoles.get("nombre"), rol.getCode()));
                // Evitar duplicados por el JOIN cuando se combina con paginación
                if (query != null) {
                    query.distinct(true);
                }
            }

            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }

    /**
     * Traduce una {@link UsuarioJpaEntity} a la entidad de dominio {@link Usuario}.
     *
     * <p>Usa {@code rebuild(...)} — nunca {@code build(...)} — porque el usuario
     * ya existe en persistencia y tiene un UUID asignado.
     * Convierte {@code RolJpaEntity.nombre} → {@link UsuarioRole#fromCode(String)}.
     */
    private Usuario toDomain(UsuarioJpaEntity entity) {
        List<UsuarioRole> roles = entity.getRoles()
                .stream()
                .map(rolEntity -> UsuarioRole.fromCode(rolEntity.getNombre()))
                .toList();

        return Usuario.rebuild(
                entity.getId(),
                entity.getKeycloakUserId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getEmail(),
                entity.getIdentificador(),
                EstadoUsuario.fromCode(entity.getEstado().getNombre()),
                roles);
    }
}
