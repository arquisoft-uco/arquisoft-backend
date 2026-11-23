package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class AvanceEvaluacionDomain {

    public static final AvanceEvaluacionDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoEvaluacion.VACIO,
            new DatosEntregable(UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilNumero.CERO),
            new ProgresoParticipantes(UtilNumero.CERO, UtilNumero.CERO, false));

    private UUID evaluacion;
    private EstadoEvaluacion estado;
    private DatosEntregable entregable;
    private ProgresoParticipantes progreso;

    private AvanceEvaluacionDomain() {}

    public static AvanceEvaluacionDomain reconstruir(
            UUID evaluacion, EstadoEvaluacion estado, DatosEntregable entregable, ProgresoParticipantes progreso) {
        var avance = new AvanceEvaluacionDomain();
        avance.evaluacion = evaluacion;
        avance.estado = estado;
        avance.entregable = entregable;
        avance.progreso = progreso;
        return avance;
    }

    public boolean puedeFinalizarse() {
        return !esVacio()
                && estado != EstadoEvaluacion.FINALIZADA
                && progreso.asesorCompleto()
                && progreso.juradosVigentes() > 0
                && progreso.juradosEvaluados() == progreso.juradosVigentes();
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }

    public DatosEntregable getEntregable() {
        return entregable;
    }

    public ProgresoParticipantes getProgreso() {
        return progreso;
    }

    public record DatosEntregable(UUID id, String proyecto, int version) {}

    public record ProgresoParticipantes(long juradosVigentes, long juradosEvaluados, boolean asesorCompleto) {}
}
