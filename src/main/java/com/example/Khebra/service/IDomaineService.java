package com.example.Khebra.service;

import com.example.Khebra.dto.DomaineDto;
import com.example.Khebra.entity.Domaine;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IDomaineService {
    void AddDomaine(Domaine domaine);
    void RemoveDomaine(int id);
    void UpdateDomaine(Domaine domaine);
    DomaineDto getDomaineById(int id);
    List<DomaineDto> getAllDomaine();
    Page<DomaineDto> getDomainesForAdmin(int page, int size);
    List<DomaineDto> getAllDomaineSignup();
}
