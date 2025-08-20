package com.example.Khebra.service.impl;

import com.example.Khebra.dto.DomaineDto;
import com.example.Khebra.entity.Domaine;
import com.example.Khebra.entity.Image;
import com.example.Khebra.exception.DomaineNotFoundException;
import com.example.Khebra.repository.DomaineRepository;
import com.example.Khebra.repository.ImageRepository;
import com.example.Khebra.service.IDomaineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DomaineService implements IDomaineService {
    private final DomaineRepository domaineRepository;
    private final ImageRepository imageRepository;

    @Override
    public void AddDomaine(Domaine domaine) {
        domaineRepository.save(domaine);
    }

    @Override
    public void RemoveDomaine(int id) {
        if (!domaineRepository.existsById(id)) {
            throw new DomaineNotFoundException("Domaine with ID :  " + id + " was not found.");
        }
        domaineRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void UpdateDomaine(Domaine domaine) {
        Domaine existing = domaineRepository.findById(domaine.getId())
                .orElseThrow(() -> new RuntimeException("Domaine introuvable"));

        existing.setName(domaine.getName());

        if (domaine.getImage() != null) {
            // Vérifier si l'image existe déjà
            Optional<Image> existingImage = (domaine.getImage().getId() != null && domaine.getImage().getId() > 0)
                    ? imageRepository.findById(domaine.getImage().getId())
                    : imageRepository.findByimageId(domaine.getImage().getImageId());

            if (existingImage.isPresent()) {
                // Mettre à jour l'image existante
                Image image = existingImage.get();
                image.setImageUrl(domaine.getImage().getImageUrl());
                existing.setImage(imageRepository.save(image));
            } else {
                // Nouvelle image
                if (existing.getImage() != null) {
                    imageRepository.delete(existing.getImage());
                }
                existing.setImage(imageRepository.save(domaine.getImage()));
            }
        } else {
            // Suppression de l'image
            if (existing.getImage() != null) {
                imageRepository.delete(existing.getImage());
                existing.setImage(null);
            }
        }

        domaineRepository.save(existing);
    }

    @Override
    public DomaineDto getDomaineById(int id) {
        Domaine domaine = domaineRepository.findById(id)
                .orElseThrow(() -> new DomaineNotFoundException("Domaine with ID : " + id + " was not found."));
        String imageUrl = domaine.getImage() != null ? domaine.getImage().getImageUrl() : null;
        return DomaineDto.builder()
                .id(domaine.getId())
                .name(domaine.getName())
                .imageUrl(imageUrl)
                .build();
    }

    @Override
    public List<DomaineDto> getAllDomaine() {
        List<Domaine> domaines = domaineRepository.findDomainesWithExperts();
        return domaines.stream()
                .map(domaine -> DomaineDto.builder()
                        .id(domaine.getId())
                        .name(domaine.getName())
                        .imageUrl(domaine.getImage() != null ? domaine.getImage().getImageUrl() : null)
                        .imageId(domaine.getId())
                        .build())
                .toList();
    }
    @Override
    public List<DomaineDto> getAllDomaineSignup() {
        List<Domaine> domaines = domaineRepository.findAll();
        return domaines.stream()
                .map(domaine -> DomaineDto.builder()
                        .id(domaine.getId())
                        .name(domaine.getName())
                        .imageUrl(domaine.getImage() != null ? domaine.getImage().getImageUrl() : null)
                        .imageId(domaine.getId())
                        .build())
                .toList();
    }

    public Page<DomaineDto> getDomainesForAdmin(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

            Page<Domaine> domainePage = domaineRepository.findAll(pageable);

            if (domainePage.isEmpty()) {
                throw new RuntimeException("No domaines found");
            }

            return domainePage.map(domaine -> DomaineDto.builder()
                    .id(domaine.getId())
                    .name(domaine.getName())
                    .imageUrl(domaine.getImage() != null ? domaine.getImage().getImageUrl() : null)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving domaines: " + e.getMessage());
        }
    }

}
