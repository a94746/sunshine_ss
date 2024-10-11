package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.service.AbstractController;
import com.vindie.sunshine_ss.common.service.PropertiesService;
import com.vindie.sunshine_ss.ui_dto.UiPic;
import com.vindie.sunshine_ss.ui_dto.UiPicInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/pic")
public class PicController extends AbstractController {
    private final PicRepo picRepo;
    private final PicService picService;
    private final PropertiesService properties;

    @PostMapping
    public UiPicInfo create(@RequestBody UiPic uiPic) {
        validateCreateRequest(uiPic);
        return picService.create(uiPic, getCurrentUser());
    }

    @GetMapping
    public List<UiPic> get(@RequestBody List<Long> ids) {
        assertTrue(!CollectionUtils.isEmpty(ids), "Pic ids is empty");
        return picService.get(ids, getCurrentUser());
    }

    @DeleteMapping
    public void delete(@RequestBody Long id) {
        assertTrue(id != null, "PicId == null");
        picService.delete(id, getCurrentUserId());
    }

    private void validateCreateRequest(UiPic uiPic) {
        uiPic.validate2();
        if (picRepo.countByOwnerId(getCurrentUserId()) >= properties.maxPicsPerAccount)
            throw new SunshineException(UiKey.PICS_MAXIMUM);
    }
}
