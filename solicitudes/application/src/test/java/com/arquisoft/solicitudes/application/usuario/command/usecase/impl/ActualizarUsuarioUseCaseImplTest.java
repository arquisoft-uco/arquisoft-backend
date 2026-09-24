package com.arquisoft.solicitudes.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarUsuarioUseCaseImpl useCase;

    @Test
    void debeActualizarYRetornarActualizada_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = UsuarioDomain.crear(id, "EST-001", "Ana Estudiante",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"));
        var entrada = UsuarioDomain.crear(id, "EST-999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.parse("2026-09-16T10:00:00Z"));
        when(usuarioPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionUsuarioResult.Actualizada.class);
        verify(usuarioOutputPort, times(1)).actualizar(any());
    }

    @Test
    void debeDescartar_cuandoElEventoEsIgualOMasViejoQueElVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");
        var vigente = UsuarioDomain.crear(id, "EST-001", "Ana Estudiante", "ana@uco.edu.co", ocurridoEn);
        var entrada = UsuarioDomain.crear(id, "EST-999", "Ana Actualizada",
                "actualizada@uco.edu.co", ocurridoEn);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionUsuarioResult.Descartada.class);
        verify(usuarioOutputPort, never()).actualizar(any());
    }

    @Test
    void debeReportarNoReplicado_cuandoElUsuarioNoExisteEnLaReplica() {
        // Arrange
        var id = UUID.randomUUID();
        var entrada = UsuarioDomain.crear(id, "EST-999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.now());
        when(usuarioPorIdFinder.obtener(id)).thenReturn(UsuarioDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionUsuarioResult.NoReplicado.class);
        verify(usuarioOutputPort, never()).actualizar(any());
    }
}
