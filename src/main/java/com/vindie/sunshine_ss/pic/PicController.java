package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.service.PropService;
import com.vindie.sunshine_ss.security.service.CurUserService;
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
public class PicController {
    private final PicRepo picRepo;
    private final PicService picService;
    private final PropService propService;

    @PostMapping
    public UiPicInfo create(@RequestBody UiPic uiPic) {
        uiPic.validate2();
        var user = CurUserService.get();
        if (picRepo.countByOwnerId(user.getId()) >= propService.maxPics)
            throw new SunshineException(UiKey.PICS_MAXIMUM);
        return picService.create(uiPic, user);
    }

    @GetMapping
    public List<UiPic> get(@RequestBody List<Long> ids) {
        assertTrue(!CollectionUtils.isEmpty(ids), "Pic ids is empty");
        return picService.get(ids, CurUserService.get());
    }

    @DeleteMapping
    public void delete(@RequestBody Long id) {
        assertTrue(id != null, "PicId == null");
        picService.delete(id, CurUserService.get());
    }
}
