package com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionExistenteEntity;
import com.arquisoft.fichas.application.representantecomite.command.finder.impl.RepresentanteComiteVigenteFinderImpl;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionExistente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionesFinderTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;

    @InjectMocks
    private EvaluacionExistenteDeRepresentanteFinderImpl evaluacionDeRepresentanteFinder;

    @InjectMocks
    private RepresentanteComiteVigenteFinderImpl representanteVigenteFinder;

    private final UUID representante = UUID.randomUUID();
    private final UUID ficha = UUID.randomUUID();

    @Test
    void debeTrasladarQueElRepresentanteYaEvaluoLaFicha_cuandoElPuertoResponde() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        var id = UUID.randomUUID();
        when(evaluacionFichaPerfilOutputPort.obtenerEvaluacionDeRepresentanteEnFicha(representante, ficha))
                .thenReturn(Optional.of(new EvaluacionExistenteEntity(id, "DESCARTADA")));

        // Act & Assert
        assertThat(evaluacionDeRepresentanteFinder.obtener(evaluacion))
                .isEqualTo(new EvaluacionExistente(id, EstadoEvaluacion.DESCARTADA));
    }

    @Test
    void debeTrasladarQueNoHayEvaluacionPrevia_cuandoElPuertoResponde() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        when(evaluacionFichaPerfilOutputPort.obtenerEvaluacionDeRepresentanteEnFicha(representante, ficha))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThat(evaluacionDeRepresentanteFinder.obtener(evaluacion)).isEqualTo(EvaluacionExistente.VACIO);
    }

    @Test
    void debeRetornarEstadoVacio_cuandoLaEvaluacionNoTieneEstado() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        var id = UUID.randomUUID();
        when(evaluacionFichaPerfilOutputPort.obtenerEvaluacionDeRepresentanteEnFicha(representante, ficha))
                .thenReturn(Optional.of(new EvaluacionExistenteEntity(id, null)));

        // Act & Assert
        assertThat(evaluacionDeRepresentanteFinder.obtener(evaluacion))
                .isEqualTo(new EvaluacionExistente(id, EstadoEvaluacion.VACIO));
    }

    @Test
    void debeTrasladarLaExistenciaDelRepresentante_cuandoExiste() {
        // Arrange
        when(representanteComiteOutputPort.existeVigentePorId(representante)).thenReturn(true);

        // Act & Assert
        assertThat(representanteVigenteFinder.obtener(representante)).isTrue();
    }

    @Test
    void debeTrasladarLaAusenciaDelRepresentante_cuandoNoExiste() {
        // Arrange
        when(representanteComiteOutputPort.existeVigentePorId(representante)).thenReturn(false);

        // Act & Assert
        assertThat(representanteVigenteFinder.obtener(representante)).isFalse();
    }
}
