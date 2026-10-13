package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarDetalleProyectoGradoAsesorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarDetalleProyectoGradoAsesorUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarDetalleProyectoGradoAsesorInteractorImplTest {

    @Mock
    private ConsultarDetalleProyectoGradoAsesorUseCase consultarDetalleProyectoGradoAsesorUseCase;

    @InjectMocks
    private ConsultarDetalleProyectoGradoAsesorInteractorImpl interactor;

    @Test
    void debeConvertirQueryACriteriaYDelegar_cuandoSeEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarDetalleProyectoGradoAsesorQuery.crear(proyectoGrado, asesor);
        var criteria = new DetalleProyectoGradoAsesorCriteria(proyectoGrado, asesor);
        var esperado = new ProyectoGradoReadModel(proyectoGrado, UtilUUID.generarNuevoUUID(), "Titulo",
                new EstadoProyectoReadModel("EN_PROCESO", "En proceso"));
        when(consultarDetalleProyectoGradoAsesorUseCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarDetalleProyectoGradoAsesorUseCase).ejecutar(criteria);
    }
}
