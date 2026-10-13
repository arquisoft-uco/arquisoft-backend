package com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.AgregacionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarAdministradorUseCaseImplTest {

    @Mock
    private AdministradorOutputPort administradorOutputPort;
    @Mock
    private AdministradorPorIdFinder administradorPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarAdministradorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAdministradorUseCaseImpl(administradorOutputPort, administradorPorIdFinder, logger);
    }

    private AdministradorDomain administrador(UUID id, Instant ocurridoEn) {
        return AdministradorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarAdministrador_cuandoNoExisteLaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var administrador = administrador(id, Instant.now());
        when(administradorPorIdFinder.obtener(id)).thenReturn(AdministradorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(administrador);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAdministradorResult.Agregada.class,
                agregada -> assertThat(agregada.administrador()).isEqualTo(id));
        verify(administradorOutputPort, times(1)).guardar(any());
        verify(administradorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDuplicada_cuandoElEventoEsMasNuevoPeroLaFilaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = Instant.now().minus(1, ChronoUnit.HOURS);
        var administrador = administrador(id, Instant.now());
        when(administradorPorIdFinder.obtener(id))
                .thenReturn(AdministradorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, null));

        // Act
        var resultado = useCase.ejecutar(administrador);

        // Assert
        assertThat(resultado).asInstanceOf(type(AgregacionAdministradorResult.Duplicada.class))
                .extracting(AgregacionAdministradorResult.Duplicada::administrador)
                .isEqualTo(id);
        verify(administradorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejoQueLaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = Instant.now();
        var administrador = administrador(id, vigente.minus(1, ChronoUnit.HOURS));
        when(administradorPorIdFinder.obtener(id))
                .thenReturn(AdministradorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, null));

        // Act
        var resultado = useCase.ejecutar(administrador);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAdministradorResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.administrador()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(vigente);
                });
        verify(administradorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoOcurridoEnEsIgual() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = Instant.now();
        var administrador = administrador(id, vigente);
        when(administradorPorIdFinder.obtener(id))
                .thenReturn(AdministradorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, null));

        // Act
        var resultado = useCase.ejecutar(administrador);

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionAdministradorResult.Descartada.class);
        verify(administradorOutputPort, never()).guardar(any());
    }

    @Test
    void debeReactivarConLosDatosDelEvento_cuandoLaFilaEstaEliminadaYElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var ocurridoEn = eliminadoEn.plusSeconds(3600);
        var entrada = AdministradorDomain.crear(id, "20161020999", "Ana Reactivada", "reactivada@uco.edu.co",
                ocurridoEn);
        when(administradorPorIdFinder.obtener(id)).thenReturn(AdministradorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAdministradorResult.Reactivada.class,
                reactivada -> assertThat(reactivada.administrador()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AdministradorEntity.class);
        verify(administradorOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Reactivada");
        assertThat(captor.getValue().email()).isEqualTo("reactivada@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(administradorOutputPort, never()).guardar(any());
        verify(administradorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartadaSinReactivar_cuandoLaFilaEstaEliminadaYElEventoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var entrada = administrador(id, eliminadoEn.minusSeconds(3600));
        when(administradorPorIdFinder.obtener(id)).thenReturn(AdministradorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAdministradorResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(eliminadoEn));
        verify(administradorOutputPort, never()).reactivar(any());
        verify(administradorOutputPort, never()).guardar(any());
    }
}
