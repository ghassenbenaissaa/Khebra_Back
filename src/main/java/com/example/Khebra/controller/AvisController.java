package com.example.Khebra.controller;

import com.example.Khebra.dto.AvisAdminDto;
import com.example.Khebra.dto.AvisDto;
import com.example.Khebra.dto.AvisRequest;
import com.example.Khebra.service.IAvisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/avis")
@RequiredArgsConstructor
public class AvisController {

    private final IAvisService avisService;
    @GetMapping("getAll")
    public List<AvisDto> getAvis(@RequestParam int expertId)  {
        return (avisService.getAvis(expertId));
    }
    @GetMapping("/all")
    public ResponseEntity<Page<AvisAdminDto>> getPagedAvis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(avisService.getAllAvis(page, size));
    }
    @PostMapping("/add")
    public ResponseEntity<Void> addAvis(@RequestBody AvisRequest avisRequest) {
        avisService.addAvis(avisRequest);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/disable")
    public ResponseEntity<Void> disableAvis(@RequestParam("avisId") int avisId) {
        avisService.disableAvis(avisId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/enable")
    public ResponseEntity<Void> enableAvis(@RequestParam("avisId") int avisId) {
        avisService.enableAvis(avisId);
        return ResponseEntity.noContent().build();
    }
}
