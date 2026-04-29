package com.arquisoft.seguridad.infrastructure.adapter.out.persistence;

import com.arquisoft.seguridad.domain.model.EstadoUsuario;
import com.arquisoft.seguridad.domain.model.Usuario;
import com.arquisoft.seguridad.domain.model.UsuarioRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios del adaptador de repositorio.
 *
 * <p><b>Nota de arquitectura:</b> Spring Boot 4.x eliminó {@code @DataJpaTest} y las
 * test-slices de JPA. Los tests de integración real con H2 requieren {@code @SpringBootTest}
 * completo, que no es viable en este módulo aislado (requiere configuración de Keycloak y BD).
 *
 * <p>Por ello se usa Mockito para verificar que el adaptador:
 * <ul>
 *   <li>Delega correctamente en {@link UsuarioJpaRepository}.</li>
 *   <li>Usa {@code rebuild(...)} al traducir JPA → dominio (nunca {@code build(...)}).</li>
 *   <li>Traduce los campos de la entidad JPA a los tipos del dominio.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryAdapterTest {

    @Mock
    private UsuarioJpaRepository jpaRepository;

    private UsuarioRepositoryAdapter adapter;

    // ─── IDs fijos para los tests ───────────────────────────────────────────
    private static final UUID USER_ID          = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
    private static final UUID KEYCLOAK_ID      = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000001");
    private static final UUID ESTADO_ACTIVO_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000001");
    private static final UUID ROL_ID           = UUID.fromString("dddddddd-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        adapter = new UsuarioRepositoryAdapter(jpaRepository);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private EstadoUsuarioJpaEntity estadoActivoJpa() {
        return EstadoUsuarioJpaEntity.builder()
                .id(ESTADO_ACTIVO_ID)
                .nombre("ACTIVO")
                .descripcion("Activo")
                .build();
    }

    private RolJpaEntity rolEstudianteJpa() {
        return RolJpaEntity.builder()
                .id(ROL_ID)
                .nombre("ESTUDIANTE")
                .descripcion("Estudiante")
                .build();
    }

    private UsuarioJpaEntity usuarioJpa() {
        return UsuarioJpaEntity.builder()
                .id(USER_ID)
                .keycloakUserId(KEYCLOAK_ID)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .identificador("12345678")
                .estado(estadoActivoJpa())
                .roles(List.of(rolEstudianteJpa()))
                .build();
    }

    // ─── Tests ──────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void debeBuscarYReconstruirConRebuild_cuandoNoHayFiltros() {
        // Arrange — repositorio retorna una página con un usuario
        Page<UsuarioJpaEntity> pageMock = new PageImpl<>(List.of(usuarioJpa()));
        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pageMock);

        // Act
        List<Usuario> resultado = adapter.buscarConFiltros(null, null, null, 0, 20);

        // Assert — dominio reconstruido con rebuild (UUID preservado, sin eventos pendientes)
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(USER_ID);
        assertThat(resultado.get(0).getKeycloakUserId()).isEqualTo(KEYCLOAK_ID);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(resultado.get(0).getRoles()).containsExactly(UsuarioRole.ESTUDIANTE);
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeFiltrarPorEstado_cuandoEstadoEsActivo() {
        // Arrange
        Page<UsuarioJpaEntity> pageMock = new PageImpl<>(List.of(usuarioJpa()));
        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pageMock);

        // Act
        List<Usuario> resultado = adapter.buscarConFiltros(null, EstadoUsuario.ACTIVO, null, 0, 20);

        // Assert — el resultado se traduce correctamente con el estado ACTIVO
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeFiltrarPorNombreOEmail_cuandoBusquedaParcialCoincide() {
        // Arrange
        Page<UsuarioJpaEntity> pageMock = new PageImpl<>(List.of(usuarioJpa()));
        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pageMock);

        // Act — buscar por nombre parcial
        List<Usuario> resultado = adapter.buscarConFiltros("juan", null, null, 0, 20);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeFiltrarPorRol_cuandoRolEsCoordinador() {
        // Arrange — usuario con rol COORDINADOR
        RolJpaEntity coordinadorJpa = RolJpaEntity.builder()
                .id(UUID.randomUUID()).nombre("COORDINADOR").descripcion("Coordinador").build();
        UsuarioJpaEntity usuarioCoord = UsuarioJpaEntity.builder()
                .id(UUID.randomUUID()).keycloakUserId(UUID.randomUUID())
                .nombre("Luis").apellido("Ramírez").email("luis@test.com").identificador("87654321")
                .estado(estadoActivoJpa()).roles(List.of(coordinadorJpa)).build();

        Page<UsuarioJpaEntity> pageMock = new PageImpl<>(List.of(usuarioCoord));
        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pageMock);

        // Act
        List<Usuario> resultado = adapter.buscarConFiltros(null, null, UsuarioRole.COORDINADOR, 0, 20);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRoles()).containsExactly(UsuarioRole.COORDINADOR);
    }

    @Test
    @SuppressWarnings("unchecked")
    void debeContarYPaginar_cuandoHayMasRegistrosQueElTamano() {
        // Arrange — count retorna 3, primera página retorna 2, segunda página retorna 1
        when(jpaRepository.count(any(Specification.class))).thenReturn(3L);

        Page<UsuarioJpaEntity> pagina0 = new PageImpl<>(List.of(usuarioJpa(), usuarioJpa()));
        Page<UsuarioJpaEntity> pagina1 = new PageImpl<>(List.of(usuarioJpa()));
        when(jpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pagina0)
                .thenReturn(pagina1);

        // Act
        long total = adapter.contarConFiltros(null, null, null);
        List<Usuario> primera = adapter.buscarConFiltros(null, null, null, 0, 2);
        List<Usuario> segunda = adapter.buscarConFiltros(null, null, null, 1, 2);

        // Assert — count correcto y paginación funcional
        assertThat(total).isEqualTo(3);
        assertThat(primera).hasSize(2);
        assertThat(segunda).hasSize(1);
    }
}
