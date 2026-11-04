package com.arquisoft.usuarios.application.jurado.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarJuradosVigentesKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.application.jurado.query.secondaryport.JuradoQueryOutputPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarJuradosVigentesUseCaseImplTest {

    @Mock
    private JuradoQueryOutputPort juradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarJuradosVigentesUseCaseImpl useCase;

    @Test
    void debeRetornarResultadoDelPuerto_cuandoConsultaVigentes() {
        // Arrange
        var criteria = JuradoVigenteCriteria.builder().pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.of(
                List.of(new JuradoVigenteReadModel(UtilUUID.generarNuevoUUID(), "1000", "Ana",
                        "ana@uco.edu.co", "3000000000", "ACTIVO")),
                0, 10, 1L);
        when(juradoQueryOutputPort.consultarVigentes(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(juradoQueryOutputPort).consultarVigentes(criteria);
    }

    @Test
    void debeLoguearEntradaYCierre_cuandoConsulta() {
        // Arrange
        var criteria = JuradoVigenteCriteria.builder().pagina(0).tamanio(10).build();
        when(juradoQueryOutputPort.consultarVigentes(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(ConsultarJuradosVigentesKey.LOG_CONSULTANDO),
                eq(criteria.getPagina()), eq(criteria.getTamanio()), eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(ConsultarJuradosVigentesKey.LOG_CONSULTA_COMPLETADA),
                eq(0L), eq(criteria.getPagina()), eq(criteria.getTamanio()));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
