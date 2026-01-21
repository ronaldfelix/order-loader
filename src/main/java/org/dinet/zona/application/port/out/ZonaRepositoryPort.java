package org.dinet.zona.application.port.out;

import org.dinet.zona.domain.model.Zona;

import java.util.Map;
import java.util.Set;

public interface ZonaRepositoryPort {
    Map<String, Zona> encontrarZonasPorIds(Set<String> zonaIds);
}
