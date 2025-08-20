package com.example.Khebra.service;

import com.example.Khebra.dto.ExpertDto;
import com.example.Khebra.entity.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IExpertService{

    Page<ExpertDto> getExperts(int page, int size, String adresse, Double minRating, Double maxRating, Double lat, Double lng, Double radiusKm,String domainName);
    Page<ExpertDto> getAllExperts(int page, int size);
    Page<ExpertDto> getExpertsForAdmin(int page, int size);
    ExpertDto getExpertByEmail(String email);
    ExpertDto mapToExpertDto(Expert expert);
    void ValiderExpert(int userId);
//    Page<ExpertDto> getExpertsByDomaine(int page, int size, String domaineExpertise);
    void updateExpert(int userId, int domaineId);
}
