package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresProyectoGradoCoordinadorUseCase;
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
class ConsultarAsesoresProyectoGradoCoordinadorInteractorImplTest {

    @Mock
    private ConsultarAsesoresProyectoGradoCoordinadorUseCase useCase;

    @InjectMocks
    private ConsultarAsesoresProyectoGradoCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConCriteria_cuandoEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var query = ConsultarAsesoresProyectoGradoCoordinadorQuery.crear(proyectoGrado, coordinador);
        var criteria = new AsesorProyectoGradoCoordinadorCriteria(proyectoGrado, coordinador);
        var esperado = List.of(new AsesorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                UtilUUID.generarNuevoUUID(), "1001", "Ana", "ana@soyuco.edu.co", "ASIGNADO", "Asignado",
                Instant.parse("2026-10-01T10:00:00Z"), true));
        when(useCase.ejecutar(criteria)).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(useCase).ejecutar(criteria);
    }
}
