package com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class RemoverAdministradorUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AdministradorOutputPort administradorOutputPort;
    @Mock
    private AdministradorPorIdFinder administradorPorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverAdministradorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAdministradorUseCaseImpl(administradorOutputPort, administradorPorIdFinder, logger);
    }

    private AdministradorDomain entrada(UUID id, Instant ocurridoEn) {
        return AdministradorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    private AdministradorDomain vigente(UUID id) {
        return AdministradorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, null);
    }

    @Test
    void debeEliminarLogicamenteYRetornarRemovida_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(administradorPorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAdministradorResult.Removida.class,
                removida -> assertThat(removida.administrador()).isEqualTo(id));
        verify(administradorPorIdFinder, times(1)).obtener(id);
        verify(administradorOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
        verify(administradorOutputPort, never()).guardar(any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoNoEsMasNuevoQueElVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(administradorPorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAdministradorResult.Descartada.class, descartada -> {
            assertThat(descartada.administrador()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        verify(administradorOutputPort, never()).eliminarLogica(any(), any());
        verify(administradorOutputPort, never()).guardar(any());
    }

    @Test
    void debeInsertarLapidaYRetornarLapida_cuandoElAdministradorNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN;
        when(administradorPorIdFinder.obtener(id)).thenReturn(AdministradorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAdministradorResult.Lapida.class,
                lapida -> assertThat(lapida.administrador()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AdministradorEntity.class);
        verify(administradorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(ocurridoEn);
        verify(administradorOutputPort, never()).eliminarLogica(any(), any());
    }
}
