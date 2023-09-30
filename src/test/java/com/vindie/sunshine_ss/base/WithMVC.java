package com.vindie.sunshine_ss.base;

import com.vindie.sunshine_ss.SunshineSsApplicationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class WithMVC extends SunshineSsApplicationTests {
    @Autowired
    private MockMvc mvc;

}
