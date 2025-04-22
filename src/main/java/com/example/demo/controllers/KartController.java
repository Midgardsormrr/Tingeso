// ======= KART CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.KartEntity;
import com.example.demo.services.KartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/karts")
public class KartController {
    @Autowired
    private KartService kartService;

    @GetMapping
    public ArrayList<KartEntity> getAllKarts() {
        return kartService.getKarts();
    }

    @PostMapping
    public KartEntity createKart(@RequestBody KartEntity kart) {
        return kartService.saveKart(kart);
    }

    @GetMapping("/{code}")
    public KartEntity getKart(@PathVariable String code) {
        return kartService.getKartByCode(code);
    }

    @PutMapping("/{code}/status")
    public KartEntity updateStatus(@PathVariable String code, @RequestParam String status) {
        return kartService.updateKartStatus(code, status);
    }

    @DeleteMapping("/{id}")
    public boolean deleteKart(@PathVariable Long id) throws Exception {
        return kartService.deleteKart(id);
    }
}