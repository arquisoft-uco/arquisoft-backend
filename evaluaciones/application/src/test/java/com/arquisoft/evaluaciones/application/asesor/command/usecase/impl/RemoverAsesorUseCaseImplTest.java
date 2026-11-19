package com.arquisoft.evaluaciones.application.asesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.evaluaciones.application.asesor.command.result.RemocionAsesorResult;
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
class RemoverAsesorUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-10-12T10:00:00Z");

    @Mock
    private AsesorOutputPort asesorOutputPort;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverAsesorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAsesorUseCaseImpl(asesorOutputPort, asesorPorIdFinder, logger);
    }

    private AsesorDomain entrada(UUID id, Instant ocurridoEn) {
        return AsesorDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private AsesorDomain guardado(UUID id) {
        return AsesorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, UtilFecha.VACIO);
    }

    @Test
    void debeRetornarRemovida_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(asesorPorIdFinder.obtener(id)).thenReturn(guardado(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Removida.class,
                removida -> assertThat(removida.asesor()).isEqualTo(id));
        verify(asesorOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeInsertarLapidaYRetornarLapida_cuandoNuncaSeRecibioElAsesor() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(asesorPorIdFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Lapida.class,
                lapida -> assertThat(lapida.asesor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AsesorEntity.class);
        verify(asesorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AsesorEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn, ocurridoEn));
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
        verify(asesorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(guardado(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.minusSeconds(60)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Descartada.class, descartada -> {
            assertThat(descartada.asesor()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
    }

    @Test
    void debeRetornarDescartada_cuandoElOcurridoEnEsIgual() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(guardado(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionAsesorResult.Descartada.class);
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
    }
}
