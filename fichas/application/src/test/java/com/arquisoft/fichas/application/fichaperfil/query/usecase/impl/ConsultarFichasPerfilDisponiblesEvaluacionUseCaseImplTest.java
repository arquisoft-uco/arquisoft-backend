package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilDisponibleEvaluacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarFichasPerfilDisponiblesEvaluacionUseCaseImplTest {

    @Mock
    private FichaPerfilDisponibleEvaluacionQueryOutputPort fichaPerfilDisponibleEvaluacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarFichasPerfilDisponiblesEvaluacionUseCaseImpl useCase;

    @Test
    void debeDelegarEnElPuertoYDevolverElResultado() {
        // Arrange
        var criteria = FichaPerfilDisponibleEvaluacionCriteria.builder()
                .pagina(0).tamanio(10).representanteComite(UtilUUID.generarNuevoUUID()).build();
        var ficha = new FichaPerfilReadModel(UtilUUID.generarNuevoUUID(), "Arquisoft Backend", null, null);
        var esperado = PaginatedResult.of(List.of(ficha), 0, 10, 1L);
        when(fichaPerfilDisponibleEvaluacionQueryOutputPort.consultarDisponiblesParaEvaluar(criteria))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(fichaPerfilDisponibleEvaluacionQueryOutputPort, times(1)).consultarDisponiblesParaEvaluar(criteria);
        verify(logger).debug(eq(FichaPerfilKey.LOG_CONSULTANDO), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(FichaPerfilKey.LOG_CONSULTA_COMPLETADA), eq(1L), eq(0), eq(10));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
