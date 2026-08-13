package com.arquisoft.fichas.domain.estudiantefichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.List;
import java.util.UUID;

public final class AgregacionEstudiantesFichaPerfilDomain {

    private List<EstudianteFichaPerfilDomain> relaciones;

    private AgregacionEstudiantesFichaPerfilDomain() {}

    public static AgregacionEstudiantesFichaPerfilDomain crear(List<EstudianteFichaPerfilDomain> relaciones) {
        var transaccion = new AgregacionEstudiantesFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setRelaciones(relaciones, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setRelaciones(List<EstudianteFichaPerfilDomain> relaciones, ValidationResult result) {
        List<EstudianteFichaPerfilDomain> lista = UtilColeccion.aplicarPorDefecto(relaciones);
        if (!DomainValidator.noVacia(lista,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTES,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTES_REQUERIDOS, result)) {
            return;
        }
        this.relaciones = lista;
    }

    public List<EstudianteFichaPerfilDomain> getRelaciones() {
        return relaciones;
    }

    public UUID getFichaPerfil() {
        return relaciones.getFirst().getFichaPerfilId();
    }

    public List<UUID> getEstudiantes() {
        return relaciones.stream().map(EstudianteFichaPerfilDomain::getEstudianteId).toList();
    }

    public int getCantidad() {
        return relaciones.size();
    }
}
