package com.arquisoft.repositorio_artefactos.domain.administrador;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class AdministradorDomain {

    public static final AdministradorDomain VACIO = AdministradorDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO,
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private AdministradorDomain() {}

    public static AdministradorDomain crear(UUID id, String identificador, String nombre, String email,
                                           Instant ocurridoEn) {
        var administrador = new AdministradorDomain();
        var result = new ValidationResult();

        administrador.setId(id, result);
        administrador.setIdentificador(identificador, result);
        administrador.setNombre(nombre, result);
        administrador.setEmail(email, result);
        administrador.setOcurridoEn(ocurridoEn, result);
        administrador.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return administrador;
    }

    public static AdministradorDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                 Instant ocurridoEn, Instant eliminadoEn) {
        var administrador = new AdministradorDomain();
        administrador.id = id;
        administrador.identificador = identificador;
        administrador.nombre = nombre;
        administrador.email = email;
        administrador.ocurridoEn = ocurridoEn;
        administrador.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return administrador;
    }

    public void remover(Instant ocurridoEn) {
        this.eliminadoEn = ocurridoEn;
        this.ocurridoEn = ocurridoEn;
    }

    public void reactivar(String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        setIdentificador(identificador, result);
        setNombre(nombre, result);
        setEmail(email, result);
        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        this.eliminadoEn = UtilFecha.VACIO;
    }

    public void actualizar(String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        setIdentificador(identificador, result);
        setNombre(nombre, result);
        setEmail(email, result);
        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
    }

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                RepositorioArtefactosFields.Administrador.ID,
                RepositorioArtefactosCodes.Administrador.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                RepositorioArtefactosFields.Administrador.IDENTIFICADOR,
                RepositorioArtefactosCodes.Administrador.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                RepositorioArtefactosFields.Administrador.NOMBRE,
                RepositorioArtefactosCodes.Administrador.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                RepositorioArtefactosFields.Administrador.EMAIL,
                RepositorioArtefactosCodes.Administrador.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                RepositorioArtefactosFields.Administrador.OCURRIDO_EN,
                RepositorioArtefactosCodes.Administrador.OCURRIDO_EN_REQUERIDO, result)) {
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

    public Instant getEliminadoEn() {
        return eliminadoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
