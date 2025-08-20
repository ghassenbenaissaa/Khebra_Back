package com.example.Khebra.controller;

import com.example.Khebra.dto.DomaineDto;
import com.example.Khebra.entity.Domaine;
import com.example.Khebra.service.impl.DomaineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("domaine")
@RequiredArgsConstructor
public class DomaineController {
    private final DomaineService domaineService;

    @GetMapping("/all")
    public ResponseEntity<List<DomaineDto>> getAllDomaines() {
         List<DomaineDto> domaines = domaineService.getAllDomaine();
         return ResponseEntity.ok(domaines);
    }
    @GetMapping("/signup/all")
    public ResponseEntity<List<DomaineDto>> getAllDomainesSignup() {
        List<DomaineDto> domaines = domaineService.getAllDomaineSignup();
        return ResponseEntity.ok(domaines);
    }
    @GetMapping("/admin")
    public ResponseEntity<Page<DomaineDto>> getExpertsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DomaineDto> domainePage = domaineService.getDomainesForAdmin(page, size);
        return ResponseEntity.ok(domainePage);
    }
    @GetMapping("get/{id}")
    public ResponseEntity<DomaineDto> getDomaineById(int id) {
        DomaineDto domaine = domaineService.getDomaineById(id);
        return ResponseEntity.ok(domaine);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addDomaine(@RequestBody Domaine domaine) {
        if (domaine.getImage() == null || domaine.getImage().getId() == 0) {
            return ResponseEntity.badRequest().build();
        }
        domaineService.AddDomaine(domaine);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateDomaine(@RequestBody Domaine domaine) {
        try {
            domaineService.UpdateDomaine(domaine);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDomaine(@PathVariable int id) {
         domaineService.RemoveDomaine(id);
         return ResponseEntity.noContent().build();
    }

}
