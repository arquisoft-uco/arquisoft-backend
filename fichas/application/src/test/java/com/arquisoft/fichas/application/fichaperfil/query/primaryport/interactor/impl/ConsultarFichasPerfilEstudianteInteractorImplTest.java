package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilEstudianteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilEstudianteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarFichasPerfilEstudianteInteractorImplTest {

    @Mock
    private ConsultarFichasPerfilEstudianteUseCase consultarFichasPerfilEstudianteUseCase;

    @InjectMocks
    private ConsultarFichasPerfilEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conCriteriaMapeado() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var query = ConsultarFichasPerfilEstudianteQuery.crear(estudiante);
        var readModel = new FichaPerfilEstudianteReadModel(
                UUID.randomUUID(), "Titulo",
                new AsesorFichaReadModel(UUID.randomUUID(), "id", "Nombre", "correo@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of());
        when(consultarFichasPerfilEstudianteUseCase.ejecutar(any(FichaPerfilEstudianteCriteria.class)))
                .thenReturn(List.of(readModel));

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).containsExactly(readModel);
        var captor = ArgumentCaptor.forClass(FichaPerfilEstudianteCriteria.class);
        verify(consultarFichasPerfilEstudianteUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeRetornarListaVacia_cuandoUseCaseNoEncuentraFichas() {
        // Arrange
        var query = ConsultarFichasPerfilEstudianteQuery.crear(UUID.randomUUID());
        when(consultarFichasPerfilEstudianteUseCase.ejecutar(any(FichaPerfilEstudianteCriteria.class)))
                .thenReturn(List.of());

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
