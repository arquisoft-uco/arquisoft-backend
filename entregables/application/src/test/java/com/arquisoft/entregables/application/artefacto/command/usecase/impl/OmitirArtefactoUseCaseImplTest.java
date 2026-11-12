package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.entregables.application.artefacto.command.result.OmisionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.OmisionArtefactoDomain;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OmitirArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoPorIdFinder artefactoPorIdFinder;

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private OmitirArtefactoUseCaseImpl useCase;

    private static ArtefactoDomain guardado(UUID id, Instant omisionOcurridaEn) {
        return ArtefactoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), "Documento", "clave",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 1, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repositorio"),
                new ArtefactoDomain.Omision(omisionOcurridaEn, omisionOcurridaEn));
    }

    @Test
    void debeMarcarOmision_cuandoElEventoEsMasNuevoQueElUltimoAplicado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-10T10:00:00Z");
        when(artefactoPorIdFinder.obtener(id)).thenReturn(guardado(id, ocurridoEn.minusSeconds(60)));

        // Act
        var resultado = useCase.ejecutar(OmisionArtefactoDomain.crear(id, ocurridoEn));

        // Assert
        verify(artefactoPorIdFinder, times(1)).obtener(id);
        verify(artefactoOutputPort).marcarOmision(eq(id), any(Instant.class), eq(ocurridoEn));
        assertThat(resultado).isEqualTo(new OmisionArtefactoResult.Omitida(id));
    }

    @Test
    void debeDevolverSinVersionCerrada_cuandoElArtefactoNoTieneFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(artefactoPorIdFinder.obtener(id)).thenReturn(ArtefactoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(OmisionArtefactoDomain.crear(id, Instant.now()));

        // Assert
        verify(artefactoOutputPort, never()).marcarOmision(any(), any(), any());
        assertThat(resultado).isEqualTo(new OmisionArtefactoResult.SinVersionCerrada(id));
    }

    @Test
    void debeDevolverDescartada_cuandoYaSeAplicoUnEventoIgualOPosterior() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-10T10:00:00Z");
        when(artefactoPorIdFinder.obtener(id)).thenReturn(guardado(id, ocurridoEn));

        // Act
        var resultado = useCase.ejecutar(OmisionArtefactoDomain.crear(id, ocurridoEn));

        // Assert
        verify(artefactoOutputPort, never()).marcarOmision(any(), any(), any());
        assertThat(resultado).isEqualTo(new OmisionArtefactoResult.Descartada(id));
    }
}
