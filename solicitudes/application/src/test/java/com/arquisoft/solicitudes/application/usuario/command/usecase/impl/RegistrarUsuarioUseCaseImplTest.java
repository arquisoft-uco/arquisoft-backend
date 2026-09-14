package com.arquisoft.solicitudes.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private AppLogger logger;

    private RegistrarUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarUsuarioUseCaseImpl(usuarioOutputPort, usuarioPorIdFinder, logger);
    }

    private UsuarioDomain usuario(UUID id, Instant ocurridoEn) {
        return UsuarioDomain.crear(id, "EST-9", "Nombre Completo", "n@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarUsuario_cuandoNoExisteLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var usuario = usuario(id, Instant.now());
        when(usuarioPorIdFinder.obtener(id)).thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(usuario);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionUsuarioResult.Agregada.class,
                agregada -> assertThat(agregada.usuario()).isEqualTo(id));
        verify(usuarioOutputPort, times(1)).guardar(any());
        verify(usuarioPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDuplicada_cuandoElEventoEsMasNuevoPeroLaFilaExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now().minus(1, ChronoUnit.HOURS);
        var usuario = usuario(id, Instant.now());
        when(usuarioPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new UsuarioEntity(id, "EST-9", "Nombre Completo", "n@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(usuario);

        // Assert
        assertThat(resultado).asInstanceOf(type(AgregacionUsuarioResult.Duplicada.class))
                .extracting(AgregacionUsuarioResult.Duplicada::usuario)
                .isEqualTo(id);
        verify(usuarioOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejoQueLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var usuario = usuario(id, vigente.minus(1, ChronoUnit.HOURS));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new UsuarioEntity(id, "EST-9", "Nombre Completo", "n@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(usuario);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionUsuarioResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.usuario()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(vigente);
                });
        verify(usuarioOutputPort, never()).guardar(any());
    }
}
