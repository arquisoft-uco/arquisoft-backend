package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoEstudianteCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoEstudianteQuery;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.ConsultarEntregablesProyectoGradoEstudianteUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEntregablesProyectoGradoEstudianteInteractorImplTest {

    @Mock
    private ConsultarEntregablesProyectoGradoEstudianteUseCase useCase;

    @InjectMocks
    private ConsultarEntregablesProyectoGradoEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirLaQueryEnCriteria_yDelegarEnElUseCase() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var query = ConsultarEntregablesProyectoGradoEstudianteQuery.crear(
                estudiante, proyecto, ConsultaCriteriaQuery.crear(1, 20, List.of(), null));
        var esperado = PaginatedResult.<EntregableProyectoGradoReadModel>of(List.of(), 1, 20, 0L);
        when(useCase.ejecutar(any(EntregableProyectoGradoEstudianteCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoEstudianteCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().consulta().getPagina()).isEqualTo(1);
        assertThat(resultado).isSameAs(esperado);
    }
}
