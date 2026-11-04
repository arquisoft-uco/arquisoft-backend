package com.arquisoft.fichas.application.estadoficha.query.usecase.impl;

import com.arquisoft.fichas.application.estadoficha.query.criteria.EstadoFichaCriteria;
import com.arquisoft.fichas.application.estadoficha.query.finder.RolesNegocioActorQueryFinder;
import com.arquisoft.fichas.application.estadoficha.query.secondaryport.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaUseCaseTest {

    @Mock
    private EstadoFichaQueryOutputPort queryOutputPort;

    @Mock
    private RolesNegocioActorQueryFinder rolesNegocioActorQueryFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosFichaUseCaseImpl useCase;

    @Test
    void debeRetornarEstadosDelPuerto_cuandoElRolTieneEstadosHabilitados() {
        // Arrange
        var roles = List.of("ASESOR_FICHA");
        var estadosEsperados = List.of(
                new EstadoFichaReadModel("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo"),
                new EstadoFichaReadModel("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion", "Lista para evaluar")
        );
        var actor = UUID.randomUUID();
        when(rolesNegocioActorQueryFinder.obtener(actor)).thenReturn(List.of());
        when(queryOutputPort.consultarPorRoles(roles)).thenReturn(estadosEsperados);

        // Act
        var resultado = useCase.ejecutar(new EstadoFichaCriteria(actor, roles));

        // Assert
        assertThat(resultado).containsExactlyElementsOf(estadosEsperados);
        verify(queryOutputPort, times(1)).consultarPorRoles(roles);
    }

    @Test
    void debeUnirSinDuplicados_cuandoRolesDeIdentidadYReplicasSeSolapan() {
        // Arrange
        var actor = UUID.randomUUID();
        var unionEsperada = List.of("COORDINADOR", "ASESOR_FICHA", "REPRESENTANTE_COMITE");
        when(rolesNegocioActorQueryFinder.obtener(actor)).thenReturn(List.of("ASESOR_FICHA", "REPRESENTANTE_COMITE"));
        when(queryOutputPort.consultarPorRoles(unionEsperada)).thenReturn(List.of());

        // Act
        useCase.ejecutar(new EstadoFichaCriteria(actor, List.of("COORDINADOR", "ASESOR_FICHA")));

        // Assert
        verify(rolesNegocioActorQueryFinder, times(1)).obtener(actor);
        verify(queryOutputPort, times(1)).consultarPorRoles(unionEsperada);
    }

    @Test
    void debeUsarSoloReplicas_cuandoLaIdentidadNoTraeRoles() {
        // Arrange
        var actor = UUID.randomUUID();
        var roles = List.of("ASESOR_FICHA");
        when(rolesNegocioActorQueryFinder.obtener(actor)).thenReturn(roles);
        when(queryOutputPort.consultarPorRoles(roles)).thenReturn(List.of());

        // Act
        useCase.ejecutar(new EstadoFichaCriteria(actor, List.of()));

        // Assert
        verify(queryOutputPort, times(1)).consultarPorRoles(roles);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveEstados() {
        // Arrange
        var roles = List.<String>of();
        var actor = UUID.randomUUID();
        when(rolesNegocioActorQueryFinder.obtener(actor)).thenReturn(List.of());
        when(queryOutputPort.consultarPorRoles(roles)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new EstadoFichaCriteria(actor, roles));

        // Assert
        assertThat(resultado).isEmpty();
        verify(queryOutputPort, times(1)).consultarPorRoles(roles);
    }
}
