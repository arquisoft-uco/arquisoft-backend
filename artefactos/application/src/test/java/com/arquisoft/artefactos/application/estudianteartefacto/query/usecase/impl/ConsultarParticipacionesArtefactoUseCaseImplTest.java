package com.arquisoft.artefactos.application.estudianteartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport.EstudianteArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
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
class ConsultarParticipacionesArtefactoUseCaseImplTest {

    @Mock
    private EstudianteArtefactoQueryOutputPort estudianteArtefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarParticipacionesArtefactoUseCaseImpl useCase;

    private static ParticipacionArtefactoReadModel readModel(String documento) {
        return new ParticipacionArtefactoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Proyecto", UtilUUID.generarNuevoUUID(), documento, 1, "Repo", UtilUUID.generarNuevoUUID(), "Ana",
                false, "Pendiente", false, false);
    }

    @Test
    void debeRetornarLaPaginaDelPuerto_cuandoHayVinculos() {
        // Arrange
        var criteria = ParticipacionArtefactoCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.of(List.of(readModel("A"), readModel("B"), readModel("C")), 0, 10, 3L);
        when(estudianteArtefactoQueryOutputPort.consultarParticipaciones(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estudianteArtefactoQueryOutputPort, times(1)).consultarParticipaciones(criteria);
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTANDO_PARTICIPACION),
                eq(criteria.getPagina()), eq(criteria.getTamanio()),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTA_PARTICIPACION_COMPLETADA), eq(3L));
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayVinculos() {
        // Arrange
        var criteria = ParticipacionArtefactoCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.<ParticipacionArtefactoReadModel>of(List.of(), 0, 10, 0L);
        when(estudianteArtefactoQueryOutputPort.consultarParticipaciones(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        verify(logger).debug(eq(EstudianteArtefactoKey.LOG_CONSULTA_PARTICIPACION_COMPLETADA), eq(0L));
    }
}
