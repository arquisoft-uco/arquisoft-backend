package com.arquisoft.artefactos.application.artefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarArtefactosEstudianteUseCaseImplTest {

    @Mock
    private ArtefactoQueryOutputPort artefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarArtefactosEstudianteUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoUnaVezYRetornarResultado_cuandoHayCoincidencias() {
        // Arrange
        var criteria = ArtefactoEstudianteCriteria.builder().pagina(0).tamanio(10).build();
        var artefacto = new ArtefactoEstudianteReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Proyecto",
                UtilUUID.generarNuevoUUID(), "Documento", 2, "Repositorio", UtilUUID.generarNuevoUUID(), "Asesor", false);
        var resultadoEsperado = PaginatedResult.of(List.of(artefacto), 0, 10, 1L);
        when(artefactoQueryOutputPort.consultarTodosEstudiante(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(artefactoQueryOutputPort, times(1)).consultarTodosEstudiante(criteria);
        verify(logger).debug(eq(ArtefactoKey.LOG_CONSULTANDO_ESTUDIANTE),
                eq(criteria.getPagina()), eq(criteria.getTamanio()),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(ArtefactoKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA),
                eq(resultadoEsperado.getTotalElements()));
    }
}
