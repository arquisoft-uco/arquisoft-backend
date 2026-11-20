package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactosEstudiantesEvaluacionFinderImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort outputPort;

    @InjectMocks
    private ContactosEstudiantesEvaluacionFinderImpl finder;

    @Test
    void debeMapearElContactoDeCadaEstudiante_cuandoSeConsultaLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var estudianteUno = UtilUUID.generarNuevoUUID();
        var estudianteDos = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.consultarContactosVigentesPorEvaluacion(evaluacion)).thenReturn(List.of(
                new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), estudianteUno, proyecto, "Uno",
                        "uno@uco.edu.co", Instant.now(), UtilFecha.VACIO),
                new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), estudianteDos, proyecto, "Dos",
                        "dos@uco.edu.co", Instant.now(), UtilFecha.VACIO)));

        // Act
        var contactos = finder.obtener(evaluacion);

        // Assert
        assertThat(contactos).containsExactly(
                new ContactoEstudiante(estudianteUno, "Uno", "uno@uco.edu.co"),
                new ContactoEstudiante(estudianteDos, "Dos", "dos@uco.edu.co"));
    }
}
