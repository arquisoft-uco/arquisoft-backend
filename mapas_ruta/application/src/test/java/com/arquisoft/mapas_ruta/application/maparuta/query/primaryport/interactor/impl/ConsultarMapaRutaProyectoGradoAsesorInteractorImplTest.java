package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaProyectoGradoCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapaRutaProyectoGradoAsesorUseCase;
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
class ConsultarMapaRutaProyectoGradoAsesorInteractorImplTest {

    @Mock
    private ConsultarMapaRutaProyectoGradoAsesorUseCase useCase;

    @InjectMocks
    private ConsultarMapaRutaProyectoGradoAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var query = ConsultarMapaRutaProyectoGradoAsesorQuery.crear(proyectoGrado);
        var criteria = new MapaRutaProyectoGradoCriteria(proyectoGrado);
        var readModel = new MapaRutaEstudianteReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                "Titulo", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        var esperado = Optional.of(readModel);
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
