package com.arquisoft.evaluaciones.domain.evaluacion.event;

import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.DatosEntregable;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.resultadoevaluacion.ResultadoEvaluacion;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFinalizadaEventTest {

    @Test
    void debeAplanarEntregableYResultado_yDescartarEstudiantesSinCorreo() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = new DatosEntregable(UtilUUID.generarNuevoUUID(), "Proyecto", 2);
        var conCorreo = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Con", "con@uco.edu.co");
        var sinCorreo = new ContactoEstudiante(UtilUUID.generarNuevoUUID(), "Sin", "");
        var coordinador = new ContactoCoordinador(UtilUUID.generarNuevoUUID(), "Coord", "coord@uco.edu.co");
        var asesor = new ContactoAsesor(UtilUUID.generarNuevoUUID(), "Asesor", "asesor@uco.edu.co");
        var destinatarios = new DestinatariosEvaluacionFinalizada(
                evaluacion, coordinador, List.of(conCorreo, sinCorreo), asesor);

        // Act
        var evento = new EvaluacionFinalizadaEvent(
                evaluacion, entregable, ResultadoEvaluacion.APROBADA_CON_OBSERVACIONES,
                new BigDecimal("3.50"), destinatarios);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Evaluaciones.EVALUACION_FINALIZADA);
        assertThat(evento.getEvaluacionId()).isEqualTo(evaluacion);
        assertThat(evento.getEntregableId()).isEqualTo(entregable.id());
        assertThat(evento.getNombreProyecto()).isEqualTo("Proyecto");
        assertThat(evento.getVersionEntregable()).isEqualTo(2);
        assertThat(evento.getResultado()).isEqualTo("APROBADA_CON_OBSERVACIONES");
        assertThat(evento.getResultadoNombre()).isEqualTo("Aprobada con observaciones");
        assertThat(evento.getNotaFinal()).isEqualByComparingTo("3.50");
        assertThat(evento.getEstudiantes()).containsExactly(conCorreo);
        assertThat(evento.getCoordinador()).isEqualTo(coordinador);
        assertThat(evento.getAsesor()).isEqualTo(asesor);
    }
}
