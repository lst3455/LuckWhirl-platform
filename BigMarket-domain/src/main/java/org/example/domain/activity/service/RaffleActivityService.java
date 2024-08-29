package org.example.domain.activity.service;

import org.example.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class RaffleActivityService extends AbstractRaffleActivity{

    public RaffleActivityService(IActivityRepository iActivityRepository) {
        super(iActivityRepository);
    }
}
