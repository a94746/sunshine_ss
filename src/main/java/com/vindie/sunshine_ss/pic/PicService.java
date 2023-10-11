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
        var account = accountRepo.getReferenceById(user.getId());
        pic.setOwner(account);

        var saved = picRepo.save(pic);
        return UiPicInfo.builder()
                .id(saved.getId())
                .lastModified(saved.getLastModified())
                .build();
    }

    public List<UiPic> get(List<Long> ids, User user) {
        return picRepo.findAllByOwnerIdAndIdIn(user.getId(), ids)
                .stream()
                .map(p -> UiPic.builder()
                        .id(p.getId())
                        .file(p.getFile())
                        .lastModified(p.getLastModified())
                        .build())
                .toList();
    }

    @Transactional
    public void delete(Long id, User user) {
        picRepo.deleteByIdAndOwnerId(id, user.getId());
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
}
