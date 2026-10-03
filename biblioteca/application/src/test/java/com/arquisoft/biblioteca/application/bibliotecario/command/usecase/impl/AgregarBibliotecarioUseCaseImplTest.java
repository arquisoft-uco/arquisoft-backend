package com.arquisoft.biblioteca.application.bibliotecario.command.usecase.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.finder.BibliotecarioPorIdFinder;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
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
class AgregarBibliotecarioUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private BibliotecarioOutputPort bibliotecarioOutputPort;
    @Mock
    private BibliotecarioPorIdFinder bibliotecarioPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarBibliotecarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarBibliotecarioUseCaseImpl(bibliotecarioOutputPort, bibliotecarioPorIdFinder, logger);
    }

    private BibliotecarioDomain entrada(UUID id, Instant ocurridoEn) {
        return BibliotecarioDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private BibliotecarioDomain guardado(UUID id, Instant eliminadoEn) {
        return BibliotecarioDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, eliminadoEn);
    }

    @Test
    void debeGuardarYRetornarAgregada_cuandoNoExisteLaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var bibliotecario = entrada(id, VIGENTE_EN);
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(BibliotecarioDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(bibliotecario);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionBibliotecarioResult.Agregada.class,
                agregada -> assertThat(agregada.bibliotecario()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(BibliotecarioEntity.class);
        verify(bibliotecarioOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new BibliotecarioEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", VIGENTE_EN, UtilFecha.VACIO));
        verify(bibliotecarioOutputPort, never()).reactivar(any());
        verify(bibliotecarioPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoOcurridoEnEsIgualAlGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionBibliotecarioResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.bibliotecario()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
                });
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDescartadaSinReactivar_cuandoLaFilaEstaEliminadaYElEventoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.minusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionBibliotecarioResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN));
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDuplicadaSinEscribir_cuandoElEventoEsMasNuevoYLaFilaEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.plusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionBibliotecarioResult.Duplicada.class,
                duplicada -> assertThat(duplicada.bibliotecario()).isEqualTo(id));
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarConLosDatosDelEvento_cuandoLaFilaEstaEliminadaYElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(bibliotecarioPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionBibliotecarioResult.Reactivada.class,
                reactivada -> assertThat(reactivada.bibliotecario()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(BibliotecarioEntity.class);
        verify(bibliotecarioOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new BibliotecarioEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn, UtilFecha.VACIO));
        verify(bibliotecarioOutputPort, never()).guardar(any());
        verify(bibliotecarioPorIdFinder, times(1)).obtener(id);
    }
}
