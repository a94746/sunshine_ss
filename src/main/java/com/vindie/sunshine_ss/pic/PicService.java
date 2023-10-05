package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.common.record.UiPicInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PicService {
    private PicRepo picRepo;

    public List<UiPicInfo> getPicInfosByOwnerId(Long ownerId) {
        return picRepo.findAllPicInfosByOwnerId(ownerId)
                .stream()
                .sorted(Comparator.comparing(UiPicInfo::getId))
                .toList();
    }

    public List<Pic> findAll() {
        return picRepo.findAll();
    }
}
