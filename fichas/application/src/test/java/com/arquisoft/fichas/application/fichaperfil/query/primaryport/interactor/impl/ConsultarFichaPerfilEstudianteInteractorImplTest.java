package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichaPerfilEstudianteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarFichaPerfilEstudianteInteractorImplTest {

    @Mock
    private ConsultarFichaPerfilEstudianteUseCase consultarFichaPerfilEstudianteUseCase;

    @InjectMocks
    private ConsultarFichaPerfilEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conCriteriaMapeado() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);
        var readModel = new FichaPerfilEstudianteReadModel(
                UUID.randomUUID(), "Titulo",
                new AsesorFichaReadModel(UUID.randomUUID(), "id", "Nombre", "correo@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of());
        when(consultarFichaPerfilEstudianteUseCase.ejecutar(any(FichaPerfilEstudianteCriteria.class)))
                .thenReturn(Optional.of(readModel));

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).containsSame(readModel);
        var captor = ArgumentCaptor.forClass(FichaPerfilEstudianteCriteria.class);
        verify(consultarFichaPerfilEstudianteUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeRetornarVacio_cuandoUseCaseNoEncuentra() {
        // Arrange
        var query = ConsultarFichaPerfilEstudianteQuery.crear(UUID.randomUUID(), UUID.randomUUID());
        when(consultarFichaPerfilEstudianteUseCase.ejecutar(any(FichaPerfilEstudianteCriteria.class)))
                .thenReturn(Optional.empty());

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
