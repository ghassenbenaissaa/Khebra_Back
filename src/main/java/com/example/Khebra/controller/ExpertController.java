package com.example.Khebra.controller;

import com.example.Khebra.dto.ExpertDto;
import com.example.Khebra.service.IExpertService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("expert")
public class ExpertController {

    private final IExpertService expertService;

    public ExpertController(IExpertService expertService) {
        this.expertService = expertService;
    }

    @GetMapping
    public ResponseEntity<Page<ExpertDto>> getExperts(
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lng", required = false) Double lng,
            @RequestParam(value = "radiusKm", required = false) Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "adresse", required = false) String adresse,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "domainName", required = false) String domainName) {


        Page<ExpertDto> expertsPage = expertService.getExperts(page, size, adresse, minRating, maxRating,lat,lng,radiusKm,domainName);
        return ResponseEntity.ok(expertsPage);
    }

//    @GetMapping("/domaineFiltre")
//    public ResponseEntity<Page<ExpertDto>> getExpertsByDomaine(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(value = "domain", required = false) String domaineExpertise) {
//
//        Page<ExpertDto> expertsPage = expertService.getExpertsByDomaine(page, size, domaineExpertise);
//        return ResponseEntity.ok(expertsPage);
//    }


    @GetMapping("/admin")
    public ResponseEntity<Page<ExpertDto>> getExpertsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ExpertDto> expertsPage = expertService.getExpertsForAdmin(page, size);
        return ResponseEntity.ok(expertsPage);
    }


    @PostMapping("/email")
    public ResponseEntity<ExpertDto> getExpertByEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        ExpertDto expertDto = expertService.getExpertByEmail(email);
        return ResponseEntity.ok(expertDto);
    }

    @PostMapping("/validation/{userId}")
    public ResponseEntity<?> ValiderExpert(@PathVariable int userId) {
        expertService.ValiderExpert(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Expert validé avec succès");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> UpdateExpert(@RequestParam int userId, @RequestParam int domaineId) {
        expertService.updateExpert(userId, domaineId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Expert modifier avec succès");
        return ResponseEntity.ok(response);
    }

}

