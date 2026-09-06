package com.arquisoft.fichas.application.estudiante.command.finder;

import com.arquisoft.fichas.application.estudiante.command.finder.impl.EstudiantesFinderImpl;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesFinderTest {

    private static final UUID ANA = UUID.randomUUID();
    private static final UUID LUIS = UUID.randomUUID();

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantesFinderImpl estudiantesFinder;

    @Test
    void debeDevolverLosEstudiantesConNombreYCorreo_cuandoElPuertoLosEncuentra() {
        // Arrange
        when(estudianteOutputPort.buscarPorIds(List.of(ANA, LUIS))).thenReturn(List.of(
                new EstudianteEntity(ANA, "1001", "Ana Gomez", "ana.gomez@soyuco.edu.co"),
                new EstudianteEntity(LUIS, "1002", "Luis Diaz", "luis.diaz@soyuco.edu.co")));

        // Act
        List<EstudianteDomain> resultado = estudiantesFinder.obtener(List.of(ANA, LUIS));

        // Assert
        assertThat(resultado)
                .extracting(EstudianteDomain::getId, EstudianteDomain::getNombre,
                        EstudianteDomain::getEmail)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(ANA, "Ana Gomez", "ana.gomez@soyuco.edu.co"),
                        org.assertj.core.groups.Tuple.tuple(LUIS, "Luis Diaz", "luis.diaz@soyuco.edu.co"));
    }

    @Test
    void debeDevolverVacioSinConsultar_cuandoLaListaLlegaVacia() {
        // Act
        List<EstudianteDomain> resultado = estudiantesFinder.obtener(List.of());

        // Assert
        assertThat(resultado).isEmpty();
        verify(estudianteOutputPort, never()).buscarPorIds(any());
    }

    @Test
    void debeDevolverVacioSinConsultar_cuandoLaListaLlegaNula() {
        // Act
        List<EstudianteDomain> resultado = estudiantesFinder.obtener(null);

        // Assert
        assertThat(resultado).isEmpty();
        verify(estudianteOutputPort, never()).buscarPorIds(any());
    }

    @Test
    void debeDevolverSoloLosEncontrados_cuandoAlgunEstudianteNoExiste() {
        // Arrange
        when(estudianteOutputPort.buscarPorIds(List.of(ANA, LUIS))).thenReturn(List.of(
                new EstudianteEntity(ANA, "1001", "Ana Gomez", "ana.gomez@soyuco.edu.co")));

        // Act
        List<EstudianteDomain> resultado = estudiantesFinder.obtener(List.of(ANA, LUIS));

        // Assert
        assertThat(resultado).extracting(EstudianteDomain::getId).containsExactly(ANA);
    }
}
