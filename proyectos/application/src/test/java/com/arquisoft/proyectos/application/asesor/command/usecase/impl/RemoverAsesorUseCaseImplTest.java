package com.arquisoft.proyectos.application.asesor.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
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

    private static final Instant ANTES = Instant.parse("2026-09-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-09-23T10:00:00Z");

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

    private AsesorDomain evento(UUID id, Instant ocurridoEn) {
        return AsesorDomain.crear(id, "1036950123", "Carlos Rios", "carlos@uco.edu.co", ocurridoEn);
    }

    private AsesorDomain replicado(UUID id, Instant ocurridoEn) {
        return AsesorDomain.reconstruir(id, "1036950123", "Carlos Rios", "carlos@uco.edu.co", ocurridoEn,
                UtilFecha.VACIO);
    }

    @Test
    void debeInsertarLapidaRemovida_cuandoElAsesorNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Lapida.class,
                lapida -> assertThat(lapida.asesor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AsesorEntity.class);
        verify(asesorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
        verify(asesorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeEliminarLogicamente_cuandoElEventoEsPosteriorAlReplicado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(replicado(id, ANTES));

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Removida.class,
                removida -> assertThat(removida.asesor()).isEqualTo(id));
        verify(asesorOutputPort, times(1)).eliminarLogica(id, DESPUES);
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoEsAnteriorAlReplicado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(replicado(id, DESPUES));

        // Act
        var resultado = useCase.ejecutar(evento(id, ANTES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Descartada.class, descartada -> {
            assertThat(descartada.asesor()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(DESPUES);
        });
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElRemovidoLlegaRepetido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var yaRemovido = AsesorDomain.reconstruir(id, "1036950123", "Carlos Rios", "carlos@uco.edu.co",
                DESPUES, DESPUES);
        when(asesorPorIdFinder.obtener(id)).thenReturn(yaRemovido);

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionAsesorResult.Descartada.class);
        verify(asesorOutputPort, never()).guardar(any());
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
    }
}
