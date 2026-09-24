package com.arquisoft.proyectos.domain.asesor;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class AsesorDomain {

    public static final AsesorDomain VACIO = AsesorDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO, Instant.EPOCH);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;

    private AsesorDomain() {}

    public static AsesorDomain crear(UUID id, String identificador, String nombre, String email,
                                           Instant ocurridoEn) {
        var asesor = new AsesorDomain();
        var result = new ValidationResult();

        asesor.setId(id, result);
        asesor.setIdentificador(identificador, result);
        asesor.setNombre(nombre, result);
        asesor.setEmail(email, result);
        asesor.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return asesor;
    }

    public static AsesorDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                 Instant ocurridoEn) {
        var asesor = new AsesorDomain();
        asesor.id = id;
        asesor.identificador = identificador;
        asesor.nombre = nombre;
        asesor.email = email;
        asesor.ocurridoEn = ocurridoEn;
        return asesor;
    }

    public void actualizar(String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        setIdentificador(identificador, result);
        setNombre(nombre, result);
        setEmail(email, result);
        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                ProyectosFields.Asesor.ID,
                ProyectosCodes.Asesor.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                ProyectosFields.Asesor.IDENTIFICADOR,
                ProyectosCodes.Asesor.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                ProyectosFields.Asesor.NOMBRE,
                ProyectosCodes.Asesor.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                ProyectosFields.Asesor.EMAIL,
                ProyectosCodes.Asesor.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                ProyectosFields.Asesor.OCURRIDO_EN,
                ProyectosCodes.Asesor.OCURRIDO_EN_REQUERIDO, result)) {
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
