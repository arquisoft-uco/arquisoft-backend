package com.arquisoft.biblioteca.domain.entregableproyectogrado;

import com.arquisoft.biblioteca.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.message.key.biblioteca.EstadoEntregableKey;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class EntregableProyectoGradoDomain {

    public static final EntregableProyectoGradoDomain VACIO = EntregableProyectoGradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilNumero.CERO, EstadoEntregable.VACIO,
            UtilTexto.VACIO, UtilTexto.VACIO);

    private static final int VERSION_MINIMA = 1;

    private UUID id;
    private UUID proyectoGrado;
    private int versionEntregable;
    private EstadoEntregable estadoEntregable;
    private String claveObjeto;
    private String nombreArchivo;

    private EntregableProyectoGradoDomain() {}

    public static EntregableProyectoGradoDomain crear(UUID id, UUID proyectoGrado, Integer versionEntregable,
                                                      String estadoEntregable,
                                                      String claveObjeto, String nombreArchivo) {
        var entregable = new EntregableProyectoGradoDomain();
        var result = new ValidationResult();

        entregable.setId(id, result);
        entregable.setProyectoGrado(proyectoGrado, result);
        entregable.setVersionEntregable(versionEntregable, result);
        entregable.setEstadoEntregable(estadoEntregable, result);
        entregable.setClaveObjeto(claveObjeto, result);
        entregable.setNombreArchivo(nombreArchivo, result);

        result.lanzarSiTieneErrores();
        return entregable;
    }

    public static EntregableProyectoGradoDomain reconstruir(UUID id, UUID proyectoGrado, int versionEntregable,
                                                            EstadoEntregable estadoEntregable,
                                                            String claveObjeto, String nombreArchivo) {
        var entregable = new EntregableProyectoGradoDomain();
        entregable.id = id;
        entregable.proyectoGrado = proyectoGrado;
        entregable.versionEntregable = versionEntregable;
        entregable.estadoEntregable = estadoEntregable;
        entregable.claveObjeto = claveObjeto;
        entregable.nombreArchivo = nombreArchivo;
        return entregable;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                BibliotecaFields.EntregableProyectoGrado.ID,
                BibliotecaCodes.EntregableProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                BibliotecaFields.EntregableProyectoGrado.PROYECTO_GRADO,
                BibliotecaCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setVersionEntregable(Integer versionEntregable, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionEntregable,
                BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE,
                BibliotecaCodes.EntregableProyectoGrado.VERSION_INVALIDA, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(versionEntregable, VERSION_MINIMA,
                BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE,
                BibliotecaCodes.EntregableProyectoGrado.VERSION_INVALIDA, result)) {
            return;
        }
        this.versionEntregable = versionEntregable;
    }

    private void setEstadoEntregable(String estadoEntregable, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(estadoEntregable,
                BibliotecaFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                BibliotecaCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        if (!EstadoEntregable.esValido(estadoEntregable)) {
            result.agregarError(
                    BibliotecaFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                    BibliotecaCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO,
                    Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO));
            return;
        }
        this.estadoEntregable = EstadoEntregable.desde(estadoEntregable);
    }

    private void setClaveObjeto(String claveObjeto, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(claveObjeto,
                BibliotecaFields.EntregableProyectoGrado.CLAVE_OBJETO,
                BibliotecaCodes.EntregableProyectoGrado.CLAVE_OBJETO_REQUERIDA, result)) {
            return;
        }
        this.claveObjeto = claveObjeto;
    }

    private void setNombreArchivo(String nombreArchivo, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombreArchivo,
                BibliotecaFields.EntregableProyectoGrado.NOMBRE_ARCHIVO,
                BibliotecaCodes.EntregableProyectoGrado.NOMBRE_ARCHIVO_REQUERIDO, result)) {
            return;
        }
        this.nombreArchivo = nombreArchivo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public int getVersionEntregable() {
        return versionEntregable;
    }

    public EstadoEntregable getEstadoEntregable() {
        return estadoEntregable;
    }

    public String getClaveObjeto() {
        return claveObjeto;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
