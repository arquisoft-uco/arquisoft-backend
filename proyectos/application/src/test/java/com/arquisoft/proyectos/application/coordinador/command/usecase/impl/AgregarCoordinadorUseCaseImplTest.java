package com.arquisoft.proyectos.application.coordinador.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
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
class AgregarCoordinadorUseCaseImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;
    @Mock
    private CoordinadorPorIdFinder coordinadorPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarCoordinadorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarCoordinadorUseCaseImpl(coordinadorOutputPort, coordinadorPorIdFinder, logger);
    }

    private CoordinadorDomain coordinador(UUID id, Instant ocurridoEn) {
        return CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarCoordinador_cuandoNoExisteLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var coordinador = coordinador(id, Instant.now());
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionCoordinadorResult.Agregada.class,
                agregada -> assertThat(agregada.coordinador()).isEqualTo(id));
        verify(coordinadorOutputPort, times(1)).guardar(any());
        verify(coordinadorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDuplicada_cuandoElEventoEsMasNuevoPeroLaFilaExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now().minus(1, ChronoUnit.HOURS);
        var coordinador = coordinador(id, Instant.now());
        when(coordinadorPorIdFinder.obtener(id))
                .thenReturn(CoordinadorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, null));

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).asInstanceOf(type(AgregacionCoordinadorResult.Duplicada.class))
                .extracting(AgregacionCoordinadorResult.Duplicada::coordinador)
                .isEqualTo(id);
        verify(coordinadorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejoQueLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var coordinador = coordinador(id, vigente.minus(1, ChronoUnit.HOURS));
        when(coordinadorPorIdFinder.obtener(id))
                .thenReturn(CoordinadorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, null));

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionCoordinadorResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.coordinador()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(vigente);
                });
        verify(coordinadorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoOcurridoEnEsIgual() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var coordinador = coordinador(id, vigente);
        when(coordinadorPorIdFinder.obtener(id))
                .thenReturn(CoordinadorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, null));

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionCoordinadorResult.Descartada.class);
        verify(coordinadorOutputPort, never()).guardar(any());
    }

    @Test
    void debeReactivarConLosDatosDelEvento_cuandoLaFilaEstaEliminadaYElEventoEsMasNuevo() {
        // Arrange
        var id = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var ocurridoEn = eliminadoEn.plusSeconds(3600);
        var entrada = CoordinadorDomain.crear(id, "20161020999", "Ana Reactivada", "reactivada@uco.edu.co",
                ocurridoEn);
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(CoordinadorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionCoordinadorResult.Reactivada.class,
                reactivada -> assertThat(reactivada.coordinador()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(CoordinadorEntity.class);
        verify(coordinadorOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Reactivada");
        assertThat(captor.getValue().email()).isEqualTo("reactivada@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(coordinadorOutputPort, never()).guardar(any());
        verify(coordinadorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartadaSinReactivar_cuandoLaFilaEstaEliminadaYElEventoEsMasViejo() {
        // Arrange
        var id = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var entrada = coordinador(id, eliminadoEn.minusSeconds(3600));
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(CoordinadorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", eliminadoEn, eliminadoEn));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionCoordinadorResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(eliminadoEn));
        verify(coordinadorOutputPort, never()).reactivar(any());
        verify(coordinadorOutputPort, never()).guardar(any());
    }
}
