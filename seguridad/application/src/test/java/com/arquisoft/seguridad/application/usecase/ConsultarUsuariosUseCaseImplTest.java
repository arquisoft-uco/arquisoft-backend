package com.arquisoft.seguridad.application.usecase;

import com.arquisoft.seguridad.application.dto.PaginaResponseDTO;
import com.arquisoft.seguridad.application.dto.UsuarioFiltroDTO;
import com.arquisoft.seguridad.application.dto.UsuarioResponseDTO;
import com.arquisoft.seguridad.domain.exception.ParametroFiltroInvalidoException;
import com.arquisoft.seguridad.domain.model.EstadoUsuario;
import com.arquisoft.seguridad.domain.model.Usuario;
import com.arquisoft.seguridad.domain.model.UsuarioRole;
import com.arquisoft.seguridad.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para {@link ConsultarUsuariosUseCaseImpl}.
 *
 * <p>Capa: application — Mockito puro, sin contexto Spring.
 * Mockea únicamente el puerto de salida {@link UsuarioRepositoryPort}.
 * No aplican tests de eventos de dominio (HU de solo lectura).
 */
@ExtendWith(MockitoExtension.class)
class ConsultarUsuariosUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private ConsultarUsuariosUseCaseImpl consultarUsuariosUseCase;

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers de construcción de fixtures
    // ─────────────────────────────────────────────────────────────────────────

    private Usuario crearUsuarioDePrueba(String nombre, EstadoUsuario estado) {
        return Usuario.rebuild(
                UUID.randomUUID(),
                UUID.randomUUID(),
                nombre,
                "Apellido",
                nombre.toLowerCase() + "@universidad.edu.co",
                "ID-" + nombre,
                estado,
                List.of(UsuarioRole.ESTUDIANTE));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void debeRetornarPagina_cuandoHayUsuarios() {
        // Arrange
        Usuario usuario = crearUsuarioDePrueba("Juan", EstadoUsuario.ACTIVO);
        UsuarioFiltroDTO filtro = new UsuarioFiltroDTO(null, null, null, 0, 20);

        when(usuarioRepositoryPort.buscarConFiltros(isNull(), isNull(), isNull(), eq(0), eq(20)))
                .thenReturn(List.of(usuario));
        when(usuarioRepositoryPort.contarConFiltros(isNull(), isNull(), isNull()))
                .thenReturn(1L);

        // Act
        PaginaResponseDTO<UsuarioResponseDTO> resultado = consultarUsuariosUseCase.ejecutar(filtro);

        // Assert — metadatos de paginación y contenido correctos
        assertThat(resultado).isNotNull();
        assertThat(resultado.totalElementos()).isEqualTo(1L);
        assertThat(resultado.numeroPagina()).isEqualTo(0);
        assertThat(resultado.tamanoPagina()).isEqualTo(20);
        assertThat(resultado.contenido()).hasSize(1);
        assertThat(resultado.contenido().get(0).nombre()).isEqualTo("Juan");

        verify(usuarioRepositoryPort, times(1))
                .buscarConFiltros(isNull(), isNull(), isNull(), eq(0), eq(20));
        verify(usuarioRepositoryPort, times(1))
                .contarConFiltros(isNull(), isNull(), isNull());
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayUsuarios() {
        // Arrange
        UsuarioFiltroDTO filtro = new UsuarioFiltroDTO(null, null, null, 0, 20);

        when(usuarioRepositoryPort.buscarConFiltros(isNull(), isNull(), isNull(), eq(0), eq(20)))
                .thenReturn(List.of());
        when(usuarioRepositoryPort.contarConFiltros(isNull(), isNull(), isNull()))
                .thenReturn(0L);

        // Act
        PaginaResponseDTO<UsuarioResponseDTO> resultado = consultarUsuariosUseCase.ejecutar(filtro);

        // Assert — lista vacía y totalElementos = 0
        assertThat(resultado.totalElementos()).isEqualTo(0L);
        assertThat(resultado.contenido()).isEmpty();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoFiltroEsInvalido() {
        // Arrange
        UsuarioFiltroDTO filtro = new UsuarioFiltroDTO(null, "ESTADO_INEXISTENTE", null, 0, 20);

        // Act / Assert — tipo de excepción + errorCode consolidados
        assertThatThrownBy(() -> consultarUsuariosUseCase.ejecutar(filtro))
                .isInstanceOf(ParametroFiltroInvalidoException.class)
                .satisfies(ex -> {
                    ParametroFiltroInvalidoException pfie = (ParametroFiltroInvalidoException) ex;
                    assertThat(pfie.getErrorCode()).isEqualTo("FILTRO_ESTADO_INVALIDO");
                });
    }

    @Test
    void debeLanzarExcepcion_cuandoRolFiltroEsInvalido() {
        // Arrange
        UsuarioFiltroDTO filtro = new UsuarioFiltroDTO(null, null, "ROL_INEXISTENTE", 0, 20);

        // Act / Assert — tipo de excepción + errorCode consolidados
        assertThatThrownBy(() -> consultarUsuariosUseCase.ejecutar(filtro))
                .isInstanceOf(ParametroFiltroInvalidoException.class)
                .satisfies(ex -> {
                    ParametroFiltroInvalidoException pfie = (ParametroFiltroInvalidoException) ex;
                    assertThat(pfie.getErrorCode()).isEqualTo("FILTRO_ROL_INVALIDO");
                });
    }
}
