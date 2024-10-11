package com.vindie.sunshine_ss.common.service;

import com.vindie.sunshine_ss.security.record.User;
import com.vindie.sunshine_ss.security.service.CurUserService;

public class AbstractController {
    
    protected User getCurrentUser() {
        return CurUserService.get();
    }

    protected Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
