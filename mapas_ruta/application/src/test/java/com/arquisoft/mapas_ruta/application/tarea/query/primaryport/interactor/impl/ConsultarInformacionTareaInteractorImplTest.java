package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarInformacionTareaQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.usecase.ConsultarInformacionTareaUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarInformacionTareaInteractorImplTest {

    @Mock
    private ConsultarInformacionTareaUseCase useCase;

    @InjectMocks
    private ConsultarInformacionTareaInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var query = ConsultarInformacionTareaQuery.crear(tarea);
        var readModel = new TareaReadModel(tarea, "Redactar", UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1), "NUEVA", "Nueva",
                UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co");
        var esperado = Optional.of(readModel);
        when(useCase.ejecutar(new TareaCriteria(tarea))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(new TareaCriteria(tarea));
    }
}
