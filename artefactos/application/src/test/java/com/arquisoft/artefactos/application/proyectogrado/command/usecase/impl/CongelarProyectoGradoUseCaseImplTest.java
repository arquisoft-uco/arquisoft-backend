package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CongelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.CongelarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.CongelacionProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CongelarProyectoGradoUseCaseImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;
    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    @Mock
    private CongelarProyectoGradoValidator congelarProyectoGradoValidator;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CongelarProyectoGradoUseCaseImpl useCase;

    @Test
    void debeMarcarEntregado_cuandoNoLoEstaba() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var congelacion = CongelacionProyectoGradoDomain.crear(id, entregadoEn);
        when(proyectoGradoPorIdFinder.obtener(id))
                .thenReturn(ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(congelacion);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, congelarProyectoGradoValidator, proyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(congelarProyectoGradoValidator).validar(new ExistenciaProyectoGrado(id, true));
        orden.verify(proyectoGradoOutputPort).marcarEntregado(id, entregadoEn);
        assertThat(resultado).isEqualTo(new CongelacionProyectoGradoResult.Congelado(id));
    }

    @Test
    void debeRetornarYaCongelado_sinMarcar() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var congelacion = CongelacionProyectoGradoDomain.crear(id, Instant.parse("2026-11-01T10:00:00Z"));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.reconstruir(
                id, "Proyecto", Instant.parse("2026-11-01T10:00:00Z"), UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(congelacion);

        // Assert
        assertThat(resultado).isEqualTo(new CongelacionProyectoGradoResult.YaCongelado(id));
        verify(proyectoGradoOutputPort, never()).marcarEntregado(any(), any());
    }

    @Test
    void debeMarcar_cuandoElExistenteEsAnteriorAlEvento() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregadoEn = Instant.parse("2026-11-02T10:00:00Z");
        var congelacion = CongelacionProyectoGradoDomain.crear(id, entregadoEn);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.reconstruir(
                id, "Proyecto", Instant.parse("2026-11-01T10:00:00Z"), UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(congelacion);

        // Assert
        verify(proyectoGradoOutputPort).marcarEntregado(id, entregadoEn);
        assertThat(resultado).isEqualTo(new CongelacionProyectoGradoResult.Congelado(id));
    }

    @Test
    void debeRechazar_cuandoValidatorLanza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var congelacion = CongelacionProyectoGradoDomain.crear(id, Instant.parse("2026-11-01T10:00:00Z"));
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(id)).when(congelarProyectoGradoValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(congelacion)).isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(proyectoGradoOutputPort, never()).marcarEntregado(any(), any());
    }
}
