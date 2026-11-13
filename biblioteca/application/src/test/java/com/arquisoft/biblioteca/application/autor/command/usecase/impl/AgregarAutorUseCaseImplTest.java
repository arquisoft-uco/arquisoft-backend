package com.arquisoft.biblioteca.application.autor.command.usecase.impl;

import com.arquisoft.biblioteca.application.autor.command.finder.AutorPorIdFinder;
import com.arquisoft.biblioteca.application.autor.command.result.AgregacionAutorResult;
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
class AgregarAutorUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AutorOutputPort autorOutputPort;
    @Mock
    private AutorPorIdFinder autorPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarAutorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAutorUseCaseImpl(autorOutputPort, autorPorIdFinder, logger);
    }

    private AutorDomain entrada(UUID id, Instant ocurridoEn) {
        return AutorDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private AutorDomain guardado(UUID id, Instant eliminadoEn) {
        return AutorDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, eliminadoEn);
    }

    @Test
    void debeGuardarYRetornarAgregada_cuandoNoExisteLaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var autor = entrada(id, VIGENTE_EN);
        when(autorPorIdFinder.obtener(id)).thenReturn(AutorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(autor);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAutorResult.Agregada.class,
                agregada -> assertThat(agregada.autor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AutorEntity.class);
        verify(autorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AutorEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", VIGENTE_EN, UtilFecha.VACIO));
        verify(autorOutputPort, never()).reactivar(any());
        verify(autorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoOcurridoEnEsIgualAlGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(autorPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAutorResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.autor()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
                });
        verify(autorOutputPort, never()).guardar(any());
        verify(autorOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDescartadaSinReactivar_cuandoLaFilaEstaEliminadaYElEventoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(autorPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.minusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAutorResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN));
        verify(autorOutputPort, never()).guardar(any());
        verify(autorOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDuplicadaSinEscribir_cuandoElEventoEsMasNuevoYLaFilaEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(autorPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.plusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAutorResult.Duplicada.class,
                duplicada -> assertThat(duplicada.autor()).isEqualTo(id));
        verify(autorOutputPort, never()).guardar(any());
        verify(autorOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarConLosDatosDelEvento_cuandoLaFilaEstaEliminadaYElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(autorPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAutorResult.Reactivada.class,
                reactivada -> assertThat(reactivada.autor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AutorEntity.class);
        verify(autorOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AutorEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn, UtilFecha.VACIO));
        verify(autorOutputPort, never()).guardar(any());
        verify(autorPorIdFinder, times(1)).obtener(id);
    }
}
