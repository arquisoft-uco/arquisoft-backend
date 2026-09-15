package com.arquisoft.proyectos.domain.estudiante;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class EstudianteDomain {

    public static final EstudianteDomain VACIO = EstudianteDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO, Instant.EPOCH);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;

    private EstudianteDomain() {}

    public static EstudianteDomain crear(UUID id, String identificador, String nombre, String email,
                                           Instant ocurridoEn) {
        var estudiante = new EstudianteDomain();
        var result = new ValidationResult();

        estudiante.setId(id, result);
        estudiante.setIdentificador(identificador, result);
        estudiante.setNombre(nombre, result);
        estudiante.setEmail(email, result);
        estudiante.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return estudiante;
    }

    public static EstudianteDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                 Instant ocurridoEn) {
        var estudiante = new EstudianteDomain();
        estudiante.id = id;
        estudiante.identificador = identificador;
        estudiante.nombre = nombre;
        estudiante.email = email;
        estudiante.ocurridoEn = ocurridoEn;
        return estudiante;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                ProyectosFields.Estudiante.ID,
                ProyectosCodes.Estudiante.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                ProyectosFields.Estudiante.IDENTIFICADOR,
                ProyectosCodes.Estudiante.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                ProyectosFields.Estudiante.NOMBRE,
                ProyectosCodes.Estudiante.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                ProyectosFields.Estudiante.EMAIL,
                ProyectosCodes.Estudiante.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                ProyectosFields.Estudiante.OCURRIDO_EN,
                ProyectosCodes.Estudiante.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
