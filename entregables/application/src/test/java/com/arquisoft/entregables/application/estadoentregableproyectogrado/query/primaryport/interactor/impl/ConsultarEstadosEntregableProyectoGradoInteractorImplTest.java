package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.criteria.EstadoEntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model.ConsultarEstadosEntregableProyectoGradoQuery;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.usecase.ConsultarEstadosEntregableProyectoGradoUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosEntregableProyectoGradoInteractorImplTest {

    @Mock
    private ConsultarEstadosEntregableProyectoGradoUseCase useCase;

    @InjectMocks
    private ConsultarEstadosEntregableProyectoGradoInteractorImpl interactor;

    @Test
    void debeConvertirLaQueryEnCriteria_yDelegarEnElUseCase() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var esperado = List.<EstadoEntregableProyectoGradoReadModel>of();
        var criteria = new EstadoEntregableProyectoGradoCriteria(entregable, asesor);
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(ConsultarEstadosEntregableProyectoGradoQuery.crear(entregable, asesor));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
