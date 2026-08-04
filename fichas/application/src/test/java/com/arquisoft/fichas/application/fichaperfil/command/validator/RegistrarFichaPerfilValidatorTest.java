package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.RegistrarFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilValidatorTest {

    @Mock
    private EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;

    @Mock
    private AsesorFichaExisteRule asesorFichaExisteRule;

    @Mock
    private FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    @Mock
    private EstudiantesExistenRule estudiantesExistenRule;

    @Mock
    private EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule;

    @InjectMocks
    private RegistrarFichaPerfilValidatorImpl validator;

    @Test
    void debeAplicarLasReglasEnOrden_cuandoHayEstudiantes() {
        // Arrange
        UUID asesor = UUID.randomUUID();
        var ficha = FichaPerfilAggregate.crear("Titulo de prueba", asesor);
        List<UUID> estudiantes = List.of(UUID.randomUUID());
        List<EstudianteFichaPerfilAggregate> relaciones =
                EstudianteFichaPerfilAggregate.crear(ficha.getId(), estudiantes);

        // Act
        validator.validar(ficha, estudiantes, relaciones);

        // Assert
        InOrder inOrder = inOrder(estudiantesSinDuplicadosRule, asesorFichaExisteRule,
                fichaPerfilTituloUnicoRule, estudiantesExistenRule, estudianteFichaPerfilCupoDisponibleRule);
        inOrder.verify(estudiantesSinDuplicadosRule).validar(estudiantes);
        inOrder.verify(asesorFichaExisteRule).validar(asesor);
        inOrder.verify(fichaPerfilTituloUnicoRule).validar(ficha.getTituloProyecto());
        inOrder.verify(estudiantesExistenRule).validar(estudiantes);
        inOrder.verify(estudianteFichaPerfilCupoDisponibleRule).validar(relaciones);
    }

    @Test
    void noDebeValidarElCupo_cuandoNoHayRelaciones() {
        // Arrange
        var ficha = FichaPerfilAggregate.crear("Titulo de prueba", UUID.randomUUID());

        // Act
        validator.validar(ficha, null, List.of());

        // Assert
        verify(estudianteFichaPerfilCupoDisponibleRule, never()).validar(anyList());
    }
}
