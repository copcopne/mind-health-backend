package com.si.mindhealth.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.si.mindhealth.dtos.request.DeletionRequestDTO;
import com.si.mindhealth.entities.DeletionRequest;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.exceptions.ForbiddenException;
import com.si.mindhealth.repositories.DeletionRequestRepository;
import com.si.mindhealth.services.DeletionRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeletionRequestServiceImpl implements DeletionRequestService {

    private final DeletionRequestRepository repository;

    @Override
    public DeletionRequest create(DeletionRequestDTO request, User user) {

        boolean exists = repository.existsByUser(user);
        if (exists)
            throw new ForbiddenException("Bạn đã yêu cầu xóa tài khoản rồi!");

        DeletionRequest deletionRequest = new DeletionRequest();
        deletionRequest.setUser(user);
        deletionRequest.setReason(request.getReason());

        return repository.save(deletionRequest);

    }

    @Override
    public void check(User user) {
        Optional<DeletionRequest> optional = repository.findByUser(user);

        if (optional.isEmpty())
            return;
        DeletionRequest request = optional.get();
        repository.delete(request);
    }
}
