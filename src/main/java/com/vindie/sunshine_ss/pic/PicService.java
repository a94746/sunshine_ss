package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.security.record.User;
import com.vindie.sunshine_ss.ui_dto.UiPic;
import com.vindie.sunshine_ss.ui_dto.UiPicInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PicService {
    private PicRepo picRepo;
    private AccountRepo accountRepo;

    public UiPicInfo create(UiPic uiPic, User user) {
        var pic = new Pic();
        pic.setFile(uiPic.getFile());
        pic.setOwner(accountRepo.getReferenceById(user.getId()));

        var saved = picRepo.save(pic);
        return UiPicInfo.builder()
                .id(saved.getId())
                .lastModified(saved.getLastModified())
                .build();
    }

    public List<UiPic> get(List<Long> ids, User user) {
        return picRepo.findAllByOwnerIdAndIdIn(user.getId(), ids)
                .stream()
                .map(this::toUiPic)
                .toList();
    }

    @Transactional
    public void delete(Long id, Long userId) {
        picRepo.deleteByIdAndOwnerId(id, userId);
    }

    public List<UiPicInfo> getPicInfosByOwnerId(Long ownerId) {
        return picRepo.findAllPicInfosByOwnerId(ownerId)
                .stream()
                .sorted(Comparator.comparing(UiPicInfo::getId))
                .toList();
    }

    public List<Pic> findAll() {
        return picRepo.findAll();
    }

    public Pic saveRepo(Pic pic) {
        return picRepo.save(pic);
    }

    private UiPic toUiPic(Pic pic) {
        return UiPic.builder()
                .id(pic.getId())
                .file(pic.getFile())
                .lastModified(pic.getLastModified())
                .build();
    }
}
