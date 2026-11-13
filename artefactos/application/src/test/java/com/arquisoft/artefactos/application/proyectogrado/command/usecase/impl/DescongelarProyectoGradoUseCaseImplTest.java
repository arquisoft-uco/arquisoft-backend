package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.DescongelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.DescongelarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.DescongelacionProyectoGradoDomain;
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
class DescongelarProyectoGradoUseCaseImplTest {

    private static final Instant CANCELADO_EN = Instant.parse("2026-11-02T10:00:00Z");

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;
    @Mock
    private ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    @Mock
    private DescongelarProyectoGradoValidator descongelarProyectoGradoValidator;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private DescongelarProyectoGradoUseCaseImpl useCase;

    @Test
    void debeDescongelar_cuandoLaCongelacionEsAnteriorAlCancelado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var descongelacion = DescongelacionProyectoGradoDomain.crear(id, CANCELADO_EN);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.reconstruir(
                id, "Proyecto", Instant.parse("2026-11-01T10:00:00Z"), UtilFecha.VACIO, null));

        // Act
        var resultado = useCase.ejecutar(descongelacion);

        // Assert
        var orden = inOrder(proyectoGradoPorIdFinder, descongelarProyectoGradoValidator, proyectoGradoOutputPort);
        orden.verify(proyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(descongelarProyectoGradoValidator).validar(new ExistenciaProyectoGrado(id, true));
        orden.verify(proyectoGradoOutputPort).limpiarEntregado(id, CANCELADO_EN);
        assertThat(resultado).isEqualTo(new DescongelacionProyectoGradoResult.Descongelado(id));
    }

    @Test
    void debeDescongelar_cuandoLaCongelacionCoincideConElCancelado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var descongelacion = DescongelacionProyectoGradoDomain.crear(id, CANCELADO_EN);
        when(proyectoGradoPorIdFinder.obtener(id))
                .thenReturn(ProyectoGradoDomain.reconstruir(id, "Proyecto", CANCELADO_EN, UtilFecha.VACIO, null));

        // Act
        var resultado = useCase.ejecutar(descongelacion);

        // Assert
        verify(proyectoGradoOutputPort).limpiarEntregado(id, CANCELADO_EN);
        assertThat(resultado).isEqualTo(new DescongelacionProyectoGradoResult.Descongelado(id));
    }

    @Test
    void debeOmitir_cuandoUnaCongelacionPosteriorYaReemplazoLaDelCancelado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var descongelacion = DescongelacionProyectoGradoDomain.crear(id, CANCELADO_EN);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.reconstruir(
                id, "Proyecto", Instant.parse("2026-11-03T10:00:00Z"), UtilFecha.VACIO, null));

        // Act
        var resultado = useCase.ejecutar(descongelacion);

        // Assert
        assertThat(resultado).isEqualTo(new DescongelacionProyectoGradoResult.Omitido(id));
        verify(proyectoGradoOutputPort, never()).limpiarEntregado(any(), any());
    }

    @Test
    void debeOmitir_cuandoElProyectoNoEstabaEntregado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var descongelacion = DescongelacionProyectoGradoDomain.crear(id, CANCELADO_EN);
        when(proyectoGradoPorIdFinder.obtener(id))
                .thenReturn(ProyectoGradoDomain.reconstruir(id, "Proyecto", UtilFecha.VACIO, UtilFecha.VACIO, null));

        // Act
        var resultado = useCase.ejecutar(descongelacion);

        // Assert
        assertThat(resultado).isEqualTo(new DescongelacionProyectoGradoResult.Omitido(id));
        verify(proyectoGradoOutputPort, never()).limpiarEntregado(any(), any());
    }

    @Test
    void noDebeEscribir_cuandoElValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var descongelacion = DescongelacionProyectoGradoDomain.crear(id, CANCELADO_EN);
        when(proyectoGradoPorIdFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new ProyectoGradoNoEncontradoException(id)).when(descongelarProyectoGradoValidator).validar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(descongelacion))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        verify(proyectoGradoOutputPort, never()).limpiarEntregado(any(), any());
    }
}
