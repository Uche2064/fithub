package com.uche.fithub.services.pack_service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uche.fithub.dto.pack_dto.AddPackSchema;
import com.uche.fithub.dto.pack_dto.PackDto;
import com.uche.fithub.dto.pack_dto.UpdatePackSchema;
import com.uche.fithub.entities.Pack;
import com.uche.fithub.repositories.PackRepository;
import com.uche.fithub.repositories.SubscriptionRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import com.uche.fithub.entities.Subscription;

@Service
public class PackServiceImpl implements IPackService {

    @Autowired
    private PackRepository packRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Override
    public PackDto addPack(AddPackSchema pack) {
        if (packRepository.findByOfferName(pack.getOfferName()).isPresent()) {
            throw new EntityExistsException("L'offre avec le nom '" + pack.getOfferName() + "' existe déjà");
        }
        Pack newPack = new Pack();
        newPack.setMonthlyPrice(pack.getMonthlyPrice());
        newPack.setOfferName(pack.getOfferName());
        newPack.setDurationMonths(pack.getDurationMonths());
        Pack savedPack = packRepository.save(newPack);
        return savedPack.getDto();
    }

    @Override
    public List<PackDto> getPacks() {
        List<Pack> packs = packRepository.findAll();
        return packs.stream().map(Pack::getDto).toList();
    }

    @Override
    public void deletePack(Long packId) {
        // Pack dbPack = packRepository.findById(packId)
        // .orElseThrow(() -> new EntityNotFoundException("L'offre avec l'id '" + packId
        // + "' n'existe pas"));

        List<Subscription> dbSubs = subscriptionRepository.findByPack(packId);
        if (!dbSubs.isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer un abonnement avec des utilisateurs qui y sont encore inscrit");
        }
        packRepository.deleteById(packId);
    }

    @Override
    public PackDto updatePack(Long packId, UpdatePackSchema pack) {
        Pack dbPack = packRepository
                .findById(packId)
                .orElseThrow(() -> new EntityNotFoundException("L'offre avec l'id '" + packId + "' n'existe pas"));

        if (pack.getOfferName() != null && pack.getOfferName().compareToIgnoreCase(dbPack.getOfferName()) == 0) {

        }
        Optional.ofNullable(pack.getOfferName()).ifPresent(dbPack::setOfferName);
        Optional.ofNullable(pack.getDurationMonths()).ifPresent(dbPack::setDurationMonths);
        Optional.ofNullable(pack.getMonthlyPrice()).ifPresent(dbPack::setMonthlyPrice);
        packRepository.save(dbPack);
        return dbPack.getDto();
    }

}
