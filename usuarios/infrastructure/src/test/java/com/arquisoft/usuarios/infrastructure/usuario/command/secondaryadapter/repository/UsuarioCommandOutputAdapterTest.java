package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioCommandOutputAdapterTest {

    @Mock
    private UsuarioCommandRepository usuarioCommandRepository;

    private UsuarioCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioCommandOutputAdapter(usuarioCommandRepository, mock(AppLogger.class));
    }

    @Test
    void debeMapearYGuardarLaEntidadComoJpaEntity_cuandoGuardaElUsuario() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new UsuarioEntity(id, "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233", "ACTIVO");

        // Act
        adapter.guardar(entity);

        // Assert
        verify(usuarioCommandRepository, times(1)).save(argThat(jpa ->
                jpa.getId().equals(id)
                        && jpa.getIdentificador().equals("usr001")
                        && jpa.getNombre().equals("Ana Pérez")
                        && jpa.getEmail().equals("ana@uco.edu.co")
                        && jpa.getContacto().equals("573001112233")
                        && jpa.getEstadoId().equals("ACTIVO")));
    }

    @Test
    void debeRetornarTrue_cuandoExistePorIdentificador() {
        // Arrange
        when(usuarioCommandRepository.existsByIdentificador("usr001")).thenReturn(true);

        // Act & Assert
        assertThat(adapter.existePorIdentificador("usr001")).isTrue();
        verify(usuarioCommandRepository, times(1)).existsByIdentificador("usr001");
    }

    @Test
    void debeRetornarFalse_cuandoNoExistePorIdentificador() {
        // Arrange
        when(usuarioCommandRepository.existsByIdentificador("usr001")).thenReturn(false);

        // Act & Assert
        assertThat(adapter.existePorIdentificador("usr001")).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoExistePorEmail() {
        // Arrange
        when(usuarioCommandRepository.existsByEmailIgnoreCase("ana@uco.edu.co")).thenReturn(true);

        // Act & Assert
        assertThat(adapter.existePorEmail("ana@uco.edu.co")).isTrue();
        verify(usuarioCommandRepository, times(1)).existsByEmailIgnoreCase("ana@uco.edu.co");
    }

    @Test
    void debeRetornarFalse_cuandoNoExistePorEmail() {
        // Arrange
        when(usuarioCommandRepository.existsByEmailIgnoreCase("ana@uco.edu.co")).thenReturn(false);

        // Act & Assert
        assertThat(adapter.existePorEmail("ana@uco.edu.co")).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoExistePorContacto() {
        // Arrange
        when(usuarioCommandRepository.existsByContacto("573001112233")).thenReturn(true);

        // Act & Assert
        assertThat(adapter.existePorContacto("573001112233")).isTrue();
        verify(usuarioCommandRepository, times(1)).existsByContacto("573001112233");
    }

    @Test
    void debeRetornarFalse_cuandoNoExistePorContacto() {
        // Arrange
        when(usuarioCommandRepository.existsByContacto("573001112233")).thenReturn(false);

        // Act & Assert
        assertThat(adapter.existePorContacto("573001112233")).isFalse();
    }

    @Test
    void debeRetornarEntidadMapeada_cuandoObtenerPorIdEncuentraElUsuario() {
        // Arrange
        var id = UUID.randomUUID();
        var jpa = UsuarioJpaEntity.builder().id(id).identificador("usr001").nombre("Ana Pérez")
                .email("ana@uco.edu.co").contacto("573001112233").estadoId("ACTIVO").build();
        when(usuarioCommandRepository.findById(id)).thenReturn(Optional.of(jpa));

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).contains(
                new UsuarioEntity(id, "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233", "ACTIVO"));
    }

    @Test
    void debeRetornarVacio_cuandoObtenerPorIdNoEncuentraElUsuario() {
        // Arrange
        var id = UUID.randomUUID();
        when(usuarioCommandRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(adapter.obtenerPorId(id)).isEmpty();
    }
}
