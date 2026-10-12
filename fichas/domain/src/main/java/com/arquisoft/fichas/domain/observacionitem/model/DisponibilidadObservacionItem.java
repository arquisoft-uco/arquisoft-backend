package com.arquisoft.fichas.domain.observacionitem.model;

import java.util.UUID;

public record DisponibilidadObservacionItem(UUID revisionItem, String observacion, long cantidadCoincidencias) {}
