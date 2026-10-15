package com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.ActualizacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarAdministradorUseCaseImplTest {

    @Mock
    private AdministradorOutputPort administradorOutputPort;
    @Mock
    private AdministradorPorIdFinder administradorPorIdFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarAdministradorUseCaseImpl useCase;

    @Test
    void debeActualizarYRetornarActualizada_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = AdministradorDomain.crear(id, "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"));
        var entrada = AdministradorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.parse("2026-09-16T10:00:00Z"));
        when(administradorPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAdministradorResult.Actualizada.class);
        verify(administradorOutputPort, times(1)).actualizar(any());
    }

    @Test
    void debeDescartar_cuandoElEventoEsIgualOMasViejoQueElVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");
        var vigente = AdministradorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var entrada = AdministradorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", ocurridoEn);
        when(administradorPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAdministradorResult.Descartada.class);
        verify(administradorOutputPort, never()).actualizar(any());
    }

    @Test
    void debeReportarNoReplicado_cuandoElAdministradorNoExisteEnLaReplica() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entrada = AdministradorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.now());
        when(administradorPorIdFinder.obtener(id)).thenReturn(AdministradorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAdministradorResult.NoReplicado.class);
        verify(administradorOutputPort, never()).actualizar(any());
    }

    @Test
    void debeConservarLaBaja_cuandoSeActualizaUnAdministradorEliminado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-01T10:00:00Z");
        var vigente = AdministradorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co",
                eliminadoEn, eliminadoEn);
        var entrada = AdministradorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.parse("2026-09-16T10:00:00Z"));
        when(administradorPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAdministradorResult.Actualizada.class);
        var captor = ArgumentCaptor.forClass(AdministradorEntity.class);
        verify(administradorOutputPort, times(1)).actualizar(captor.capture());
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Actualizada");
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(eliminadoEn);
    }
}
