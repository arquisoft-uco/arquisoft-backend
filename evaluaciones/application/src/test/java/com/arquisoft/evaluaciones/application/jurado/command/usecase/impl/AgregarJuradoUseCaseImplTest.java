package com.arquisoft.evaluaciones.application.jurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.jurado.command.finder.JuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.jurado.command.result.AgregacionJuradoResult;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;
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
class AgregarJuradoUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private JuradoOutputPort juradoOutputPort;
    @Mock
    private JuradoPorIdFinder juradoPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarJuradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarJuradoUseCaseImpl(juradoOutputPort, juradoPorIdFinder, logger);
    }

    private JuradoDomain entrada(UUID id, Instant ocurridoEn) {
        return JuradoDomain.crear(id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn);
    }

    private JuradoDomain guardado(UUID id, Instant eliminadoEn) {
        return JuradoDomain.reconstruir(
                id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, eliminadoEn);
    }

    @Test
    void debeGuardarYRetornarAgregada_cuandoNoExisteLaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var jurado = entrada(id, VIGENTE_EN);
        when(juradoPorIdFinder.obtener(id)).thenReturn(JuradoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(jurado);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionJuradoResult.Agregada.class,
                agregada -> assertThat(agregada.jurado()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(JuradoEntity.class);
        verify(juradoOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new JuradoEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", VIGENTE_EN, UtilFecha.VACIO));
        verify(juradoOutputPort, never()).reactivar(any());
        verify(juradoPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDescartadaSinEscribir_cuandoOcurridoEnEsIgualAlGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(juradoPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionJuradoResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.jurado()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
                });
        verify(juradoOutputPort, never()).guardar(any());
        verify(juradoOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDescartadaSinReactivar_cuandoLaFilaEstaEliminadaYElEventoEsMasViejo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(juradoPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.minusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionJuradoResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN));
        verify(juradoOutputPort, never()).guardar(any());
        verify(juradoOutputPort, never()).reactivar(any());
    }

    @Test
    void debeRetornarDuplicadaSinEscribir_cuandoElEventoEsMasNuevoYLaFilaEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(juradoPorIdFinder.obtener(id)).thenReturn(guardado(id, null));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN.plusSeconds(3600)));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionJuradoResult.Duplicada.class,
                duplicada -> assertThat(duplicada.jurado()).isEqualTo(id));
        verify(juradoOutputPort, never()).guardar(any());
        verify(juradoOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarConLosDatosDelEvento_cuandoLaFilaEstaEliminadaYElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(juradoPorIdFinder.obtener(id)).thenReturn(guardado(id, VIGENTE_EN));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionJuradoResult.Reactivada.class,
                reactivada -> assertThat(reactivada.jurado()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(JuradoEntity.class);
        verify(juradoOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new JuradoEntity(
                id, "20161020999", "Ana Nueva", "nueva@uco.edu.co", ocurridoEn, UtilFecha.VACIO));
        verify(juradoOutputPort, never()).guardar(any());
        verify(juradoPorIdFinder, times(1)).obtener(id);
    }
}
