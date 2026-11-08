package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ProyectoGradoDomain {

    public static final ProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, EstadoProyecto.VACIO);

    private UUID id;
    private String nombre;
    private EstadoProyecto estadoProyecto;

    private ProyectoGradoDomain() {}

    public static ProyectoGradoDomain crear(UUID id, String nombre) {
        var proyecto = new ProyectoGradoDomain();
        var result = new ValidationResult();
        proyecto.setId(id, result);
        proyecto.setNombre(nombre, result);
        proyecto.setEstadoProyectoInicial();
        result.lanzarSiTieneErrores();
        return proyecto;
    }

    public static ProyectoGradoDomain reconstruir(UUID id, String nombre, EstadoProyecto estadoProyecto) {
        var proyecto = new ProyectoGradoDomain();
        proyecto.id = id;
        proyecto.nombre = nombre;
        proyecto.estadoProyecto = estadoProyecto;
        return proyecto;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                EntregablesFields.ProyectoGrado.NOMBRE,
                EntregablesCodes.ProyectoGrado.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEstadoProyectoInicial() {
        this.estadoProyecto = EstadoProyecto.EN_PROCESO;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
