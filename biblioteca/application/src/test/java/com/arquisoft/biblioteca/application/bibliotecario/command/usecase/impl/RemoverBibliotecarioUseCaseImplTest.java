package com.arquisoft.biblioteca.application.bibliotecario.command.usecase.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.finder.BibliotecarioPorIdFinder;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
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
class RemoverBibliotecarioUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private BibliotecarioOutputPort bibliotecarioOutputPort;
    @Mock
    private BibliotecarioPorIdFinder bibliotecarioPorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverBibliotecarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverBibliotecarioUseCaseImpl(bibliotecarioOutputPort, bibliotecarioPorIdFinder, logger);
    }

    private BibliotecarioDomain entrada(UUID id, Instant ocurridoEn) {
        return BibliotecarioDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private BibliotecarioDomain guardado(UUID id, Instant eliminadoEn) {
        return BibliotecarioDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, eliminadoEn);
    }

    @Test
    void debeGuardarLaLapidaYRetornarLapida_cuandoNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T11:00:00Z");
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(BibliotecarioDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionBibliotecarioResult.Lapida.class,
                lapida -> assertThat(lapida.bibliotecario()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(BibliotecarioEntity.class);
        verify(bibliotecarioOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new BibliotecarioEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn, ocurridoEn));
        verify(bibliotecarioOutputPort, never()).eliminarLogica(any(), any());
        verify(bibliotecarioPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeEliminarLogicaConElOcurridoEnDelEventoYRetornarRemovida_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T11:00:00Z");
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(guardado(id, UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionBibliotecarioResult.Removida.class,
                removida -> assertThat(removida.bibliotecario()).isEqualTo(id));
        verify(bibliotecarioOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeVolverAMarcarRemovida_cuandoLaReplicaYaEstabaEliminadaConUnEventoMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T12:00:00Z");
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionBibliotecarioResult.Removida.class);
        verify(bibliotecarioOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoOcurridoEnNoEsPosteriorAlGuardado() {
        // Arrange
        var idIgual = UtilUUID.generarNuevoUUID();
        var idAnterior = UtilUUID.generarNuevoUUID();
        when(bibliotecarioPorIdFinder.obtener(idIgual)).thenReturn(guardado(idIgual, UtilFecha.VACIO));
        when(bibliotecarioPorIdFinder.obtener(idAnterior)).thenReturn(guardado(idAnterior, UtilFecha.VACIO));

        // Act
        var igual = useCase.ejecutar(entrada(idIgual, VIGENTE_EN));
        var anterior = useCase.ejecutar(entrada(idAnterior, VIGENTE_EN.minusSeconds(60)));

        // Assert
        assertThat(igual).isInstanceOfSatisfying(RemocionBibliotecarioResult.Descartada.class, descartada -> {
            assertThat(descartada.bibliotecario()).isEqualTo(idIgual);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        assertThat(anterior).isInstanceOfSatisfying(RemocionBibliotecarioResult.Descartada.class, descartada -> {
            assertThat(descartada.bibliotecario()).isEqualTo(idAnterior);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioOutputPort, never()).eliminarLogica(any(), any());
    }
}
