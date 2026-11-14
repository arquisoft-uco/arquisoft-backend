package com.arquisoft.evaluaciones.application.entregable.command.usecase.impl;

import com.arquisoft.evaluaciones.application.entregable.command.finder.EntregableExisteFinder;
import com.arquisoft.evaluaciones.application.entregable.command.result.AgregacionEntregableResult;
import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.EntregableOutputPort;
import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.entity.EntregableEntity;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEntregableUseCaseImplTest {

    @Mock
    private EntregableOutputPort entregableOutputPort;

    @Mock
    private EntregableExisteFinder entregableExisteFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarEntregableUseCaseImpl useCase;

    @Test
    void debeGuardarYDevolverAgregado_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var entregable = EntregableDomain.crear(id, proyectoGrado, "Sistema de riego", 2);
        when(entregableExisteFinder.obtener(id)).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(entregable);

        // Assert
        var orden = inOrder(entregableExisteFinder, logger, entregableOutputPort);
        var captor = ArgumentCaptor.forClass(EntregableEntity.class);
        orden.verify(entregableExisteFinder, times(1)).obtener(id);
        orden.verify(logger).debug(EntregableKey.LOG_VERIFICACION_AGREGAR, id, false);
        orden.verify(entregableOutputPort).guardar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new EntregableEntity(id, proyectoGrado, "Sistema de riego", 2));
        assertThat(resultado).isInstanceOfSatisfying(AgregacionEntregableResult.Agregado.class,
                agregado -> assertThat(agregado.entregable()).isEqualTo(id));
    }

    @Test
    void debeDevolverDuplicadoSinGuardar_cuandoYaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = EntregableDomain.crear(id, UtilUUID.generarNuevoUUID(), "Sistema de riego", 2);
        when(entregableExisteFinder.obtener(id)).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(entregable);

        // Assert
        verify(logger).debug(EntregableKey.LOG_VERIFICACION_AGREGAR, id, true);
        verify(entregableOutputPort, never()).guardar(any());
        assertThat(resultado).isInstanceOfSatisfying(AgregacionEntregableResult.Duplicado.class,
                duplicado -> assertThat(duplicado.entregable()).isEqualTo(id));
    }
}
