package com.si.mindhealth.services;

import com.si.mindhealth.dtos.request.LoginRequestDTO;
import com.si.mindhealth.dtos.request.RefreshRequestDTO;
import com.si.mindhealth.dtos.response.CredenticalsResponseDTO;

public interface AuthService {
    CredenticalsResponseDTO loginHandler(LoginRequestDTO request) throws Exception;

    CredenticalsResponseDTO refreshHandler(RefreshRequestDTO request);
}
