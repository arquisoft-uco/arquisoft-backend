package com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoEstudianteCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoQueryOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.query.validator.ConsultarEntregablesProyectoGradoEstudianteValidator;
import com.arquisoft.entregables.application.proyectogrado.query.finder.EstudiantePerteneceProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.proyectogrado.query.finder.model.EstudianteProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEntregablesProyectoGradoEstudianteUseCaseImplTest {

    @Mock
    private EstudiantePerteneceProyectoGradoQueryFinder finder;

    @Mock
    private ConsultarEntregablesProyectoGradoEstudianteValidator validator;

    @Mock
    private EntregableProyectoGradoQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEntregablesProyectoGradoEstudianteUseCaseImpl useCase;

    @Test
    void debeValidarAntesDeLeer_yRetornarElResultado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var consulta = EntregableProyectoGradoCriteria.builder().pagina(2).tamanio(5).build();
        var criteria = new EntregableProyectoGradoEstudianteCriteria(estudiante, proyecto, consulta);
        var esperado = PaginatedResult.<EntregableProyectoGradoReadModel>of(List.of(), 2, 5, 11L);
        when(finder.obtener(new EstudianteProyectoGrado(estudiante, proyecto))).thenReturn(true);
        when(outputPort.consultar(consulta)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(logger, finder, validator, outputPort);
        orden.verify(logger).debug(eq(EntregableProyectoGradoKey.LOG_CONSULTANDO_ESTUDIANTE),
                eq(proyecto), eq(2), eq(5), eq(false), eq(false));
        orden.verify(finder, times(1)).obtener(new EstudianteProyectoGrado(estudiante, proyecto));
        orden.verify(validator).validar(estudiante, proyecto, true);
        orden.verify(outputPort, times(1)).consultar(consulta);
        orden.verify(logger).debug(eq(EntregableProyectoGradoKey.LOG_CONSULTA_COMPLETADA),
                eq(11L), eq(2), eq(5));
    }
}
