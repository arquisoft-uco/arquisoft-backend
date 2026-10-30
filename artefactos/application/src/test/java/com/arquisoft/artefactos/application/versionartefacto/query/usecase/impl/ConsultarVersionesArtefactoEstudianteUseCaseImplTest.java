package com.arquisoft.artefactos.application.versionartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVersionesArtefactoEstudianteUseCaseImplTest {

    @Mock
    private VersionArtefactoQueryOutputPort versionArtefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarVersionesArtefactoEstudianteUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoUnaVezYRetornarResultado_cuandoHayCoincidencias() {
        // Arrange
        var criteria = VersionArtefactoEstudianteCriteria.builder().pagina(0).tamanio(10).build();
        var version = new VersionArtefactoReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, Instant.now(),
                "uri/v2.pdf", "Proyecto Uno", "Documento Uno", 1, "repo-uno", "Asesor Uno",
                UtilUUID.generarNuevoUUID(), "PENDIENTE", "Pendiente");
        var resultadoEsperado = PaginatedResult.of(List.of(version), 0, 10, 1L);
        when(versionArtefactoQueryOutputPort.consultarTodasEstudiante(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(versionArtefactoQueryOutputPort, times(1)).consultarTodasEstudiante(criteria);
        verify(logger).debug(eq(VersionArtefactoKey.LOG_CONSULTANDO_ESTUDIANTE),
                eq(criteria.getPagina()), eq(criteria.getTamanio()),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(VersionArtefactoKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA),
                eq(resultadoEsperado.getTotalElements()));
    }
}
