package com.example.Khebra.service;

import com.example.Khebra.dto.AvisAdminDto;
import com.example.Khebra.dto.AvisDto;
import com.example.Khebra.dto.AvisRequest;
import com.example.Khebra.entity.Avis;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IAvisService {

    List<AvisDto> getAvis(int id);
    Page<AvisAdminDto> getAllAvis(int page, int size);
    void addAvis(AvisRequest avis);
    void disableAvis(int id);
    void enableAvis(int id);
    void updateRate(int expertId);
}
