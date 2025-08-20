package com.example.Khebra.projections;

import com.example.Khebra.entity.StatusDemande;

public interface StatusCountProjection {
    StatusDemande getStatus();
    Long getCount();
}
