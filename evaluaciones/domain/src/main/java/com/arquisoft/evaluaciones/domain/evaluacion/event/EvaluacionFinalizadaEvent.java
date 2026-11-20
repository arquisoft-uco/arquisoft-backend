package com.arquisoft.evaluaciones.domain.evaluacion.event;

import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacion.AvanceEvaluacionDomain.DatosEntregable;
import com.arquisoft.evaluaciones.domain.evaluacion.model.DestinatariosEvaluacionFinalizada;
import com.arquisoft.evaluaciones.domain.resultadoevaluacion.ResultadoEvaluacion;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class EvaluacionFinalizadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Evaluaciones.EVALUACION_FINALIZADA;
    public static final String EVENT_TYPE = "EvaluacionFinalizadaEvent";

    private final UUID evaluacionId;
    private final UUID entregableId;
    private final String nombreProyecto;
    private final int versionEntregable;
    private final String resultado;
    private final String resultadoNombre;
    private final BigDecimal notaFinal;
    private final ContactoCoordinador coordinador;
    private final List<ContactoEstudiante> estudiantes;
    private final ContactoAsesor asesor;

    public EvaluacionFinalizadaEvent(
            UUID evaluacionId,
            DatosEntregable entregable,
            ResultadoEvaluacion resultado,
            BigDecimal notaFinal,
            DestinatariosEvaluacionFinalizada destinatarios) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.evaluacionId = evaluacionId;
        this.entregableId = entregable.id();
        this.nombreProyecto = entregable.proyecto();
        this.versionEntregable = entregable.version();
        this.resultado = resultado.getId();
        this.resultadoNombre = resultado.getNombre();
        this.notaFinal = notaFinal;
        this.coordinador = destinatarios.coordinador();
        this.estudiantes = List.copyOf(destinatarios.estudiantesConCorreo());
        this.asesor = destinatarios.asesor();
    }

    public UUID getEvaluacionId() {
        return evaluacionId;
    }

    public UUID getEntregableId() {
        return entregableId;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public int getVersionEntregable() {
        return versionEntregable;
    }

    public String getResultado() {
        return resultado;
    }

    public String getResultadoNombre() {
        return resultadoNombre;
    }

    public BigDecimal getNotaFinal() {
        return notaFinal;
    }

    public ContactoCoordinador getCoordinador() {
        return coordinador;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
