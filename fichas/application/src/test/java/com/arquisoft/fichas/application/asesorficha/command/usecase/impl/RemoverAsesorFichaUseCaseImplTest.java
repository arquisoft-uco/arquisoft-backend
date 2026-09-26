package com.arquisoft.fichas.application.asesorficha.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
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
class RemoverAsesorFichaUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-09-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;
    @Mock
    private AsesorFichaPorIdFinder asesorFichaPorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverAsesorFichaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAsesorFichaUseCaseImpl(asesorFichaOutputPort, asesorFichaPorIdFinder, logger);
    }

    private AsesorFichaDomain evento(UUID id, Instant ocurridoEn) {
        return AsesorFichaDomain.crear(id, "1036950123", "Laura Gomez", "laura@uco.edu.co", ocurridoEn);
    }

    private AsesorFichaDomain replicado(UUID id, Instant ocurridoEn) {
        return AsesorFichaDomain.reconstruir(id, "1036950123", "Laura Gomez", "laura@uco.edu.co", ocurridoEn,
                UtilFecha.VACIO);
    }

    @Test
    void debeInsertarLapidaRemovida_cuandoElAsesorFichaNuncaSeReplico() {
        // Arrange
        var id = UUID.randomUUID();
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(AsesorFichaDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorFichaResult.Lapida.class,
                lapida -> assertThat(lapida.asesorFicha()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AsesorFichaEntity.class);
        verify(asesorFichaOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        verify(asesorFichaOutputPort, never()).eliminarLogica(any(), any());
        verify(asesorFichaPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeEliminarLogicamente_cuandoElEventoEsPosteriorAlReplicado() {
        // Arrange
        var id = UUID.randomUUID();
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(replicado(id, ANTES));

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorFichaResult.Removida.class,
                removida -> assertThat(removida.asesorFicha()).isEqualTo(id));
        verify(asesorFichaOutputPort, times(1)).eliminarLogica(id, DESPUES);
        verify(asesorFichaOutputPort, never()).guardar(any());
        verify(asesorFichaPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoEsAnteriorAlReplicado() {
        // Arrange
        var id = UUID.randomUUID();
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(replicado(id, DESPUES));

        // Act
        var resultado = useCase.ejecutar(evento(id, ANTES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorFichaResult.Descartada.class, descartada -> {
            assertThat(descartada.asesorFicha()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(DESPUES);
        });
        verify(asesorFichaOutputPort, never()).guardar(any());
        verify(asesorFichaOutputPort, never()).eliminarLogica(any(), any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElRemovidoLlegaRepetido() {
        // Arrange
        var id = UUID.randomUUID();
        var yaRemovido = AsesorFichaDomain.reconstruir(id, "1036950123", "Laura Gomez", "laura@uco.edu.co",
                DESPUES, DESPUES);
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(yaRemovido);

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionAsesorFichaResult.Descartada.class);
        verify(asesorFichaOutputPort, never()).guardar(any());
        verify(asesorFichaOutputPort, never()).eliminarLogica(any(), any());
    }
}
