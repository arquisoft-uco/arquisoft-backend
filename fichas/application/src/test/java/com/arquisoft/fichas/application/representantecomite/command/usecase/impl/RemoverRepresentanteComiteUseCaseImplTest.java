package com.arquisoft.fichas.application.representantecomite.command.usecase.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComitePorIdFinder;
import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
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
class RemoverRepresentanteComiteUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-09-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-09-24T10:00:00Z");
    private static final Instant MAS_TARDE = Instant.parse("2026-09-26T10:00:00Z");

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;
    @Mock
    private RepresentanteComitePorIdFinder representanteComitePorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverRepresentanteComiteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverRepresentanteComiteUseCaseImpl(representanteComiteOutputPort,
                representanteComitePorIdFinder, logger);
    }

    private RepresentanteComiteDomain evento(UUID id, Instant ocurridoEn) {
        return RepresentanteComiteDomain.crear(id, "20161020123", "Ana Pérez", "ana.perez@uco.edu.co", ocurridoEn);
    }

    private RepresentanteComiteDomain replicado(UUID id, Instant ocurridoEn, Instant eliminadoEn) {
        return RepresentanteComiteDomain.reconstruir(id, "20161020123", "Ana Pérez", "ana.perez@uco.edu.co",
                ocurridoEn, eliminadoEn);
    }

    @Test
    void debeInsertarLapidaRemovida_cuandoElRepresentanteComiteNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionRepresentanteComiteResult.Lapida.class,
                lapida -> assertThat(lapida.representanteComite()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(RepresentanteComiteEntity.class);
        verify(representanteComiteOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        verify(representanteComiteOutputPort, never()).eliminarLogica(any(), any());
        verify(representanteComitePorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeEliminarLogicamente_cuandoElEventoEsPosteriorAlReplicadoVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(replicado(id, ANTES, UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionRepresentanteComiteResult.Removida.class,
                removida -> assertThat(removida.representanteComite()).isEqualTo(id));
        verify(representanteComiteOutputPort, times(1)).eliminarLogica(id, DESPUES);
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComitePorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeVolverAMarcarLaBaja_cuandoLlegaUnRemovidoMasNuevoSobreUnaBajaPrevia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(replicado(id, DESPUES, DESPUES));

        // Act
        var resultado = useCase.ejecutar(evento(id, MAS_TARDE));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionRepresentanteComiteResult.Removida.class);
        verify(representanteComiteOutputPort, times(1)).eliminarLogica(id, MAS_TARDE);
        verify(representanteComiteOutputPort, never()).guardar(any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoEsAnteriorAlReplicado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(replicado(id, DESPUES, UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(evento(id, ANTES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionRepresentanteComiteResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.representanteComite()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(DESPUES);
                });
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComiteOutputPort, never()).eliminarLogica(any(), any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElRemovidoLlegaRepetido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(replicado(id, DESPUES, DESPUES));

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionRepresentanteComiteResult.Descartada.class);
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComiteOutputPort, never()).eliminarLogica(any(), any());
    }
}
