package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoAsesorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoAsesorUseCase;
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
class ConsultarProyectosGradoAsesorInteractorImplTest {

    @Mock
    private ConsultarProyectosGradoAsesorUseCase consultarProyectosGradoAsesorUseCase;

    @InjectMocks
    private ConsultarProyectosGradoAsesorInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoSeEjecuta() {
        // Arrange
        var query = ConsultarProyectosGradoAsesorQuery.crear(UtilUUID.generarNuevoUUID(),
                ConsultaCriteriaQuery.crear(3, 20, List.of(), null));
        var esperado = PaginatedResult.of(List.<ProyectoGradoReadModel>of(), 3, 20, 0L);
        when(consultarProyectosGradoAsesorUseCase.ejecutar(any(ProyectoGradoAsesorCriteria.class)))
                .thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(ProyectoGradoAsesorCriteria.class);
        verify(consultarProyectosGradoAsesorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(3);
        assertThat(captor.getValue().getTamanio()).isEqualTo(20);
        assertThat(captor.getValue().tieneFiltros()).isTrue();
    }
}
