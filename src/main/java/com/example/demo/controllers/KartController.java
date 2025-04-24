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

    @GetMapping("/{Id}")
    public KartEntity getKart(@PathVariable Long Id) {
        return kartService.getKartById(Id);
    }

    @PutMapping("/{Id}/status")
    public KartEntity updateStatus(@PathVariable Long Id, @RequestParam String status) {
        return kartService.updateKartStatus(Id, status);
    }

    @DeleteMapping("/{id}")
    public boolean deleteKart(@PathVariable Long id) throws Exception {
        return kartService.deleteKart(id);
    }
}