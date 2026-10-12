package com.arquisoft.fichas.application.representantecomite.command.usecase.impl;

import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComitePorIdFinder;
import com.arquisoft.fichas.application.representantecomite.command.result.AgregacionRepresentanteComiteResult;
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
class AgregarRepresentanteComiteUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-09-20T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;
    @Mock
    private RepresentanteComitePorIdFinder representanteComitePorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarRepresentanteComiteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarRepresentanteComiteUseCaseImpl(
                representanteComiteOutputPort, representanteComitePorIdFinder, logger);
    }

    private RepresentanteComiteDomain entrada(UUID id, Instant ocurridoEn) {
        return RepresentanteComiteDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private RepresentanteComiteDomain replicado(UUID id, Instant ocurridoEn, Instant eliminadoEn) {
        return RepresentanteComiteDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, eliminadoEn);
    }

    @Test
    void debeGuardarYRetornarAgregada_cuandoNoExisteLaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionRepresentanteComiteResult.Agregada.class,
                agregada -> assertThat(agregada.representanteComite()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(RepresentanteComiteEntity.class);
        verify(representanteComiteOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(representanteComitePorIdFinder, times(1)).obtener(id);
        verify(representanteComiteOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoOcurridoEnEsIgualAlVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(replicado(id, DESPUES, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionRepresentanteComiteResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.representanteComite()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(DESPUES);
                });
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComiteOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDescartadaSinReactivar_cuandoElEventoEsMasViejoQueLaBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var lapida = replicado(id, DESPUES, DESPUES);
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(lapida);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ANTES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionRepresentanteComiteResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(DESPUES));
        assertThat(lapida.estaEliminado()).isTrue();
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComiteOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDuplicadaSinEscribir_cuandoElEventoEsMasNuevoYLaFilaEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(replicado(id, ANTES, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionRepresentanteComiteResult.Duplicada.class,
                duplicada -> assertThat(duplicada.representanteComite()).isEqualTo(id));
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComiteOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarConLosDatosDelEvento_cuandoLaFilaEstaEliminadaYElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(representanteComitePorIdFinder.obtener(id)).thenReturn(replicado(id, ANTES, ANTES));

        // Act
        var resultado = useCase.ejecutar(entrada(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionRepresentanteComiteResult.Reactivada.class,
                reactivada -> assertThat(reactivada.representanteComite()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(RepresentanteComiteEntity.class);
        verify(representanteComiteOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Nueva");
        assertThat(captor.getValue().email()).isEqualTo("nueva@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComitePorIdFinder, times(1)).obtener(id);
    }
}
