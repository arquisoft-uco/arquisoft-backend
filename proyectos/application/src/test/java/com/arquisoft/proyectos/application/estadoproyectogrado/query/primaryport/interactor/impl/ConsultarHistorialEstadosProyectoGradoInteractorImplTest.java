package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarHistorialEstadosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.ConsultarHistorialEstadosProyectoGradoUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarHistorialEstadosProyectoGradoInteractorImplTest {

    @Mock
    private ConsultarHistorialEstadosProyectoGradoUseCase useCase;

    @InjectMocks
    private ConsultarHistorialEstadosProyectoGradoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElCriteria() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        var query = ConsultarHistorialEstadosProyectoGradoQuery.crear(proyectoGrado, usuario);
        var criteria = new HistorialEstadosProyectoGradoCriteria(proyectoGrado, usuario);
        var esperado = List.of(new EstadoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "ATRASADO",
                "Atrasado", Instant.parse("2026-10-01T10:00:00Z"), null));
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
