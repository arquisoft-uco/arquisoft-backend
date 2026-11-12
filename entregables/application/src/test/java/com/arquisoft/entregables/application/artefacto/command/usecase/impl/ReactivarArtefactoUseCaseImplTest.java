package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.entregables.application.artefacto.command.result.ReactivacionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class ReactivarArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoPorIdFinder artefactoPorIdFinder;

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ReactivarArtefactoUseCaseImpl useCase;

    private static ArtefactoDomain guardado(UUID id, Instant omisionOcurridaEn) {
        return ArtefactoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), "Documento", "clave",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 1, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repositorio"),
                new ArtefactoDomain.Omision(omisionOcurridaEn, omisionOcurridaEn));
    }

    @Test
    void debeLimpiarOmision_cuandoElEventoEsMasNuevoQueElUltimoAplicado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-10T10:00:00Z");
        when(artefactoPorIdFinder.obtener(id)).thenReturn(guardado(id, ocurridoEn.minusSeconds(60)));

        // Act
        var resultado = useCase.ejecutar(ReactivacionArtefactoDomain.crear(id, ocurridoEn));

        // Assert
        verify(artefactoPorIdFinder, times(1)).obtener(id);
        verify(artefactoOutputPort).limpiarOmision(id, ocurridoEn);
        assertThat(resultado).isEqualTo(new ReactivacionArtefactoResult.Reactivada(id));
    }

    @Test
    void debeDevolverSinVersionCerrada_cuandoElArtefactoNoTieneFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(artefactoPorIdFinder.obtener(id)).thenReturn(ArtefactoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(ReactivacionArtefactoDomain.crear(id, Instant.now()));

        // Assert
        verify(artefactoOutputPort, never()).limpiarOmision(any(), any());
        assertThat(resultado).isEqualTo(new ReactivacionArtefactoResult.SinVersionCerrada(id));
    }

    @Test
    void debeDevolverDescartada_cuandoYaSeAplicoUnEventoIgualOPosterior() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-10T10:00:00Z");
        when(artefactoPorIdFinder.obtener(id)).thenReturn(guardado(id, ocurridoEn.plusSeconds(5)));

        // Act
        var resultado = useCase.ejecutar(ReactivacionArtefactoDomain.crear(id, ocurridoEn));

        // Assert
        verify(artefactoOutputPort, never()).limpiarOmision(any(), any());
        assertThat(resultado).isEqualTo(new ReactivacionArtefactoResult.Descartada(id));
    }
}
