package com.arquisoft.fichas.domain.fichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RegistroFichaPerfilDomain {

    private FichaPerfilDomain ficha;
    private EstadoFichaPerfilDomain estadoInicial;
    private AgregacionEstudiantesFichaPerfilDomain estudiantes;

    private RegistroFichaPerfilDomain() {}

    public static RegistroFichaPerfilDomain crear(FichaPerfilDomain ficha,
                                                  EstadoFichaPerfilDomain estadoInicial,
                                                  AgregacionEstudiantesFichaPerfilDomain estudiantes) {
        var transaccion = new RegistroFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setFicha(ficha, result);
        transaccion.setEstadoInicial(estadoInicial, result);
        transaccion.setEstudiantes(estudiantes, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setFicha(FichaPerfilDomain ficha, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ficha,
                FichasFields.FichaPerfil.ID,
                FichasCodes.FichaPerfil.ID_REQUERIDO, result)) {
            return;
        }
        this.ficha = ficha;
    }

    private void setEstadoInicial(EstadoFichaPerfilDomain estadoInicial, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estadoInicial,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.estadoInicial = estadoInicial;
    }

    private void setEstudiantes(AgregacionEstudiantesFichaPerfilDomain estudiantes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiantes,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.estudiantes = estudiantes;
    }

    public FichaPerfilDomain getFicha() {
        return ficha;
    }

    public EstadoFichaPerfilDomain getEstadoInicial() {
        return estadoInicial;
    }

    public AgregacionEstudiantesFichaPerfilDomain getEstudiantes() {
        return estudiantes;
    }

    public UUID getFichaPerfil() {
        return ficha.getId();
    }
}
