package com.arquisoft.fichas.application.representantecomite.command.usecase.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComitePorIdFinder;
import com.arquisoft.fichas.application.representantecomite.command.result.ActualizacionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
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
class ActualizarRepresentanteComiteUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-09-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-09-16T10:00:00Z");

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;
    @Mock
    private RepresentanteComitePorIdFinder representanteComitePorIdFinder;
    @Mock
    private AppLogger logger;

    private ActualizarRepresentanteComiteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarRepresentanteComiteUseCaseImpl(
                representanteComiteOutputPort, representanteComitePorIdFinder, logger);
    }

    private RepresentanteComiteDomain entrada(UUID id, Instant ocurridoEn) {
        return RepresentanteComiteDomain.crear(
                id, "20161020999", "Ana Actualizada", "actualizada@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeActualizarConservandoLaBaja_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = RepresentanteComiteDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", ANTES, ANTES);
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ActualizacionRepresentanteComiteResult.Actualizada.class,
                actualizada -> assertThat(actualizada.representanteComite()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(RepresentanteComiteEntity.class);
        verify(representanteComiteOutputPort, times(1)).actualizar(captor.capture());
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Actualizada");
        assertThat(captor.getValue().email()).isEqualTo("actualizada@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(ANTES);
        verify(representanteComitePorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoElEventoEsIgualOMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigente = RepresentanteComiteDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", DESPUES, null);
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ActualizacionRepresentanteComiteResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.representanteComite()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(DESPUES);
                });
        verify(representanteComiteOutputPort, never()).actualizar(any());
    }

    @Test
    void debeReportarNoReplicadoSinEscribir_cuandoElRepresentanteNoExisteEnLaReplica() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ActualizacionRepresentanteComiteResult.NoReplicado.class,
                noReplicado -> assertThat(noReplicado.representanteComite()).isEqualTo(id));
        verify(representanteComiteOutputPort, never()).actualizar(any());
    }
}
