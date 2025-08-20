package com.example.Khebra.service.impl;

import com.example.Khebra.dto.ExpertDto;
import com.example.Khebra.dto.ImageDto;
import com.example.Khebra.entity.Domaine;
import com.example.Khebra.entity.Expert;
import com.example.Khebra.exception.ExpertNotFoundException;
import com.example.Khebra.repository.DomaineRepository;
import com.example.Khebra.repository.ExpertRepository;
import com.example.Khebra.repository.UserRepository;
import com.example.Khebra.service.IExpertService;
import com.example.Khebra.specifications.ExpertSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class ExpertService implements IExpertService {
    private final ExpertRepository repo;
    private final UserRepository userRepository;
    private final DomaineRepository domaineRepository;


    public Page<ExpertDto> getAllExperts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());
        Page<Expert> expertPage = repo.findAll(pageable);

        return expertPage.map(this::mapToExpertDto);
    }

    public ExpertDto getExpertByEmail(String email){
        Expert expert = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Expert not found with email: " + email));
        return mapToExpertDto(expert);
    }


    public Page<ExpertDto> getExperts(int page, int size, String adresse, Double minRating, Double maxRating, Double lat, Double lng, Double radiusKm,String domainName) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());

            Specification<Expert> spec =
                    ExpertSpecifications.hasAdresse(adresse)
                            .and(ExpertSpecifications.hasRatingBetween(minRating, maxRating))
                            .and(ExpertSpecifications.hasValidAccount())
                            .and(ExpertSpecifications.isNotBanned())
                            .and(ExpertSpecifications.hasLocationWithinRadius(lat, lng, radiusKm))
                            .and(ExpertSpecifications.hasDomaine(domainName));

            Page<Expert> expertPage = repo.findAll(spec, pageable);

            if (expertPage.isEmpty()) {
                throw new ExpertNotFoundException("Aucun expert n'est trouver");
            }

            return expertPage.map(this::mapToExpertDto);

        } catch (ExpertNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving experts: " + e.getMessage());
        }
    }

//    public Page<ExpertDto> getExpertsByDomaine(int page, int size, String domaineExpertise) {
//        try {
//            Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());
//            Page<Expert> expertPage = repo.findByDomaine(domaineExpertise, pageable);
//            if (expertPage.isEmpty()) {
//                throw new ExpertNotFoundException("No experts found with provided filters");
//            }
//            return expertPage.map(this::mapToExpertDto);
//        } catch (ExpertNotFoundException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RuntimeException("Error retrieving experts: " + e.getMessage());
//        }
//    }



    public Page<ExpertDto> getExpertsForAdmin(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());

            Specification<Expert> spec = ExpertSpecifications.isActive();
            Page<Expert> expertPage = repo.findAll(spec, pageable);

            if (expertPage.isEmpty()) {
                throw new ExpertNotFoundException("No experts found");
            }
            return expertPage.map(this::mapToExpertDto);

        } catch (ExpertNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving experts: " + e.getMessage());
        }
    }


    public ExpertDto mapToExpertDto(Expert expert) {
        ImageDto imageDto = null;
        if (expert.getImage() != null) {
            imageDto = ImageDto.builder()
                    .imageUrl(expert.getImage().getImageUrl())
                    .build();
        }

        return ExpertDto.builder()
                .id(expert.getId())
                .firstname(expert.getFirstname())
                .lastname(expert.getLastname())
                .numTel(expert.getNumTel())
                .cin(expert.getCin())
                .image(imageDto)
                .email(expert.getEmail())
                .adresse(expert.getAdresse())
                .expertise(expert.getExpertise())
                .domaineExpertise(expert.getDomaine().getName())
                .biographie(expert.getBiographie())
                .rating(expert.getRating())
                .isValidated(expert.isValidated())
                .isBanned(expert.isBanned())
                .build();
    }

    public void ValiderExpert(int userId) {
        Expert expert = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Expert not found"));
        expert.setValidated(true);
        userRepository.save(expert);
    }


    public void updateExpert(int userId, int domaineId) {
        Expert existingExpert = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Expert not found with id: " + userId));
        Domaine newDomaine = domaineRepository.findById(domaineId)
                .orElseThrow(() -> new RuntimeException("Domaine not found with id: " + domaineId));
        existingExpert.setDomaine(newDomaine);
        repo.save(existingExpert);
    }


}
