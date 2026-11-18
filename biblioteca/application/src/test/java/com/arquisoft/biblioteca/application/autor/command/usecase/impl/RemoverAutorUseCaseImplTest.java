package com.arquisoft.biblioteca.application.autor.command.usecase.impl;

import com.arquisoft.biblioteca.application.autor.command.finder.AutorPorIdFinder;
import com.arquisoft.biblioteca.application.autor.command.result.RemocionAutorResult;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.AutorOutputPort;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.entity.AutorEntity;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;
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
class RemoverAutorUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AutorOutputPort autorOutputPort;
    @Mock
    private AutorPorIdFinder autorPorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverAutorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAutorUseCaseImpl(autorOutputPort, autorPorIdFinder, logger);
    }

    private AutorDomain entrada(UUID id, Instant ocurridoEn) {
        return AutorDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private AutorDomain guardado(UUID id, Instant eliminadoEn) {
        return AutorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, eliminadoEn);
    }

    @Test
    void debeGuardarLaLapidaYRetornarLapida_cuandoNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T11:00:00Z");
        when(autorPorIdFinder.obtener(id)).thenReturn(AutorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAutorResult.Lapida.class,
                lapida -> assertThat(lapida.autor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AutorEntity.class);
        verify(autorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AutorEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn, ocurridoEn));
        verify(autorOutputPort, never()).eliminarLogica(any(), any());
        verify(autorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeEliminarLogicaConElOcurridoEnDelEventoYRetornarRemovida_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T11:00:00Z");
        when(autorPorIdFinder.obtener(id)).thenReturn(guardado(id, UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAutorResult.Removida.class,
                removida -> assertThat(removida.autor()).isEqualTo(id));
        verify(autorOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
        verify(autorOutputPort, never()).guardar(any());
        verify(autorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeVolverAMarcarRemovida_cuandoLaReplicaYaEstabaEliminadaConUnEventoMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T12:00:00Z");
        when(autorPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionAutorResult.Removida.class);
        verify(autorOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoOcurridoEnNoEsPosteriorAlGuardado() {
        // Arrange
        var idIgual = UtilUUID.generarNuevoUUID();
        var idAnterior = UtilUUID.generarNuevoUUID();
        when(autorPorIdFinder.obtener(idIgual)).thenReturn(guardado(idIgual, UtilFecha.VACIO));
        when(autorPorIdFinder.obtener(idAnterior)).thenReturn(guardado(idAnterior, UtilFecha.VACIO));

        // Act
        var igual = useCase.ejecutar(entrada(idIgual, VIGENTE_EN));
        var anterior = useCase.ejecutar(entrada(idAnterior, VIGENTE_EN.minusSeconds(60)));

        // Assert
        assertThat(igual).isInstanceOfSatisfying(RemocionAutorResult.Descartada.class, descartada -> {
            assertThat(descartada.autor()).isEqualTo(idIgual);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        assertThat(anterior).isInstanceOfSatisfying(RemocionAutorResult.Descartada.class, descartada -> {
            assertThat(descartada.autor()).isEqualTo(idAnterior);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        verify(autorOutputPort, never()).guardar(any());
        verify(autorOutputPort, never()).eliminarLogica(any(), any());
    }
}
