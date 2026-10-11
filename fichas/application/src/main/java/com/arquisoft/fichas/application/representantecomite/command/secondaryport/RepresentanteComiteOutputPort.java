package com.arquisoft.fichas.application.representantecomite.command.secondaryport;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RepresentanteComiteOutputPort {

    boolean existeVigentePorId(UUID id);

    Optional<RepresentanteComiteEntity> obtenerPorId(UUID id);

    void guardar(RepresentanteComiteEntity representanteComite);

    void actualizar(RepresentanteComiteEntity representanteComite);

    void reactivar(RepresentanteComiteEntity representanteComite);

    void eliminarLogica(UUID id, Instant ocurridoEn);
}
