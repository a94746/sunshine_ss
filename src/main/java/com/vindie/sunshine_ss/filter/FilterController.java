package com.vindie.sunshine_ss.filter;

import com.vindie.sunshine_ss.filter.service.FilterService;
import com.vindie.sunshine_ss.security.service.CurUserService;
import com.vindie.sunshine_ss.ui_dto.UiMyFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/filter")
public class FilterController {
    private FilterService filterService;

    @PutMapping("/edit_my")
    public void editMy(@RequestBody UiMyFilter uiMyFilter) {
        filterService.editMy(uiMyFilter, CurUserService.get());
    }
}
