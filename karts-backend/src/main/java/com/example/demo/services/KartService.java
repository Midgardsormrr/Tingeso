// ======= KART SERVICE =======
package com.example.demo.services;

import com.example.demo.entities.KartEntity;
import com.example.demo.repositories.KartRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class KartService {
    @Autowired
    private KartRepository kartRepository;

    public ArrayList<KartEntity> getKarts() {
        return (ArrayList<KartEntity>) kartRepository.findAll();
    }

    public KartEntity saveKart(KartEntity kart) {
        return kartRepository.save(kart);
    }

    public KartEntity getKartById(Long Id) {
        return kartRepository.findById(Id).orElse(null);
    }

    public KartEntity updateKartStatus(Long Id, String status) {
        KartEntity kart = getKartById(Id);
        if (kart != null) {
            kart.setStatus(status);
            return kartRepository.save(kart);
        }
        return null;
    }

    public boolean deleteKart(Long id) throws Exception {
        try {
            kartRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}