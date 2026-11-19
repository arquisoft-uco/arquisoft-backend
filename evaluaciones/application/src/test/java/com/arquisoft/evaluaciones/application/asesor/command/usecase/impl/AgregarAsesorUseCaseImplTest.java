package com.arquisoft.evaluaciones.application.asesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.evaluaciones.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.evaluaciones.domain.asesor.AsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
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
class AgregarAsesorUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AsesorOutputPort asesorOutputPort;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarAsesorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAsesorUseCaseImpl(asesorOutputPort, asesorPorIdFinder, logger);
    }

    private AsesorDomain entrada(UUID id, Instant ocurridoEn) {
        return AsesorDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private AsesorDomain guardado(UUID id, Instant eliminadoEn) {
        return AsesorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, eliminadoEn);
    }

    @Test
    void debeGuardarYRetornarAgregada_cuandoNoExisteLaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = entrada(id, VIGENTE_EN);
        when(asesorPorIdFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(asesor);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorResult.Agregada.class,
                agregada -> assertThat(agregada.asesor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AsesorEntity.class);
        verify(asesorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AsesorEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", VIGENTE_EN, UtilFecha.VACIO));
        verify(asesorOutputPort, never()).reactivar(any());
        verify(asesorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoOcurridoEnEsIgualAlGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.asesor()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
                });
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDescartadaSinReactivar_cuandoLaFilaEstaEliminadaYElEventoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.minusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN));
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDuplicadaSinEscribir_cuandoElEventoEsMasNuevoYLaFilaEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.plusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorResult.Duplicada.class,
                duplicada -> assertThat(duplicada.asesor()).isEqualTo(id));
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarConLosDatosDelEvento_cuandoLaFilaEstaEliminadaYElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(asesorPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorResult.Reactivada.class,
                reactivada -> assertThat(reactivada.asesor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AsesorEntity.class);
        verify(asesorOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AsesorEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn, UtilFecha.VACIO));
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorPorIdFinder, times(1)).obtener(id);
    }
}
