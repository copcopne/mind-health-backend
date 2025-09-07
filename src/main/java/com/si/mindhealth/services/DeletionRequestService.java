package com.si.mindhealth.services;

import com.si.mindhealth.dtos.request.DeletionRequestDTO;
import com.si.mindhealth.entities.DeletionRequest;
import com.si.mindhealth.entities.User;

public interface DeletionRequestService {

    DeletionRequest create(DeletionRequestDTO request, User user);

    void check(User user);
}
