package com.arquisoft.fichas.application.estadofichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaPerfilAsesorUseCaseImplTest {

    @Mock
    private EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosFichaPerfilAsesorUseCaseImpl useCase;

    @Test
    void debeConsultarPuertoYRetornarResultado_cuandoSeEjecuta() {
        // Arrange
        var criteria = EstadoFichaPerfilAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var estado = new EstadoFichaPerfilAsesorReadModel(
                UUID.randomUUID(), "Proyecto A", "APROBADA", "Aprobada", Instant.now());
        var resultadoEsperado = PaginatedResult.of(List.of(estado), 0, 10, 1L);

        when(estadoFichaPerfilQueryOutputPort.consultarPorAsesor(criteria)).thenReturn(resultadoEsperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        assertThat(resultado.getContent()).hasSize(1);
        verify(estadoFichaPerfilQueryOutputPort).consultarPorAsesor(criteria);
    }

    @Test
    void debeLoguearEntradaYCierre_conEstadoFichaPerfilKey() {
        // Arrange
        var criteria = EstadoFichaPerfilAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var resultado = PaginatedResult.of(List.<EstadoFichaPerfilAsesorReadModel>of(), 0, 10, 0L);
        when(estadoFichaPerfilQueryOutputPort.consultarPorAsesor(criteria)).thenReturn(resultado);

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(EstadoFichaPerfilKey.LOG_CONSULTANDO_ASESOR), eq(0), eq(10),
                eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(EstadoFichaPerfilKey.LOG_CONSULTA_ASESOR_COMPLETADA), eq(0L), eq(0), eq(10));
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElPuertoNoEncuentraEstados() {
        // Arrange
        var criteria = EstadoFichaPerfilAsesorCriteria.builder().pagina(0).tamanio(10).build();
        var resultadoVacio = PaginatedResult.of(List.<EstadoFichaPerfilAsesorReadModel>of(), 0, 10, 0L);
        when(estadoFichaPerfilQueryOutputPort.consultarPorAsesor(any(EstadoFichaPerfilAsesorCriteria.class)))
                .thenReturn(resultadoVacio);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
