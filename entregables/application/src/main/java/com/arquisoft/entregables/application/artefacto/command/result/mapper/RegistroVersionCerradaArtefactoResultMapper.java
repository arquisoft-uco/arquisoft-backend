package com.arquisoft.entregables.application.artefacto.command.result.mapper;

import com.arquisoft.entregables.application.artefacto.command.result.RegistroVersionCerradaArtefactoResult;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;

public final class RegistroVersionCerradaArtefactoResultMapper {

    private RegistroVersionCerradaArtefactoResultMapper() {}

    public static RegistroVersionCerradaArtefactoResult.Registrada toResultRegistrada(
            RegistroVersionCerradaArtefactoDomain registro) {
        return new RegistroVersionCerradaArtefactoResult.Registrada(registro.getArtefacto().getId());
    }

    public static RegistroVersionCerradaArtefactoResult.Obsoleta toResultObsoleta(
            RegistroVersionCerradaArtefactoDomain registro) {
        return new RegistroVersionCerradaArtefactoResult.Obsoleta(registro.getArtefacto().getId());
    }

    public static RegistroVersionCerradaArtefactoResult.ProyectoCerrado toResultProyectoCerrado(
            RegistroVersionCerradaArtefactoDomain registro) {
        return new RegistroVersionCerradaArtefactoResult.ProyectoCerrado(registro.getArtefacto().getId());
    }

    public static RegistroVersionCerradaArtefactoResult.OrigenNoDisponible toResultOrigenNoDisponible(
            RegistroVersionCerradaArtefactoDomain registro) {
        var artefacto = registro.getArtefacto();
        return new RegistroVersionCerradaArtefactoResult.OrigenNoDisponible(
                artefacto.getId(), artefacto.getNombreDocumento(), registro.getClaveOrigen());
    }
}
