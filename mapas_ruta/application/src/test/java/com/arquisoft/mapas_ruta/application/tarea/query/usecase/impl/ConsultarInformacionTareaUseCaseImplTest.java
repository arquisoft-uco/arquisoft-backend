package com.arquisoft.mapas_ruta.application.tarea.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.secondaryport.TareaQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarInformacionTareaUseCaseImplTest {

    @Mock
    private TareaQueryOutputPort tareaQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarInformacionTareaUseCaseImpl useCase;

    @Test
    void debeDevolverElReadModel_cuandoLaTareaExiste() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var readModel = new TareaReadModel(tarea, "Redactar", UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1), "NUEVA", "Nueva",
                UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co");
        when(tareaQueryOutputPort.consultarPorId(tarea)).thenReturn(Optional.of(readModel));

        // Act
        var resultado = useCase.ejecutar(new TareaCriteria(tarea));

        // Assert
        assertThat(resultado).containsSame(readModel);
        var orden = inOrder(logger, tareaQueryOutputPort);
        orden.verify(logger).debug(TareaKey.LOG_CONSULTANDO_INFORMACION, tarea);
        orden.verify(tareaQueryOutputPort).consultarPorId(tarea);
        orden.verify(logger).debug(TareaKey.LOG_CONSULTA_INFORMACION_COMPLETADA, true);
        verify(tareaQueryOutputPort, times(1)).consultarPorId(tarea);
    }

    @Test
    void debeDevolverVacio_cuandoLaTareaNoExiste() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        when(tareaQueryOutputPort.consultarPorId(tarea)).thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(new TareaCriteria(tarea));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(TareaKey.LOG_CONSULTA_INFORMACION_COMPLETADA, false);
    }
}
