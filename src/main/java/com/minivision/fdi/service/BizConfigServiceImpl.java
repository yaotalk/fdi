package com.minivision.fdi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.minivision.fastdfs.FdfsService;
import com.minivision.fdi.common.BeanPropertyUtils;
import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.event.BizConfigUpdateEvent;
import com.minivision.fdi.repository.BizConfigRepository;
import com.minivision.fdi.rest.param.CreateBizConfigParam;
import com.minivision.fdi.rest.param.QueryBizConfigParam;
import com.minivision.fdi.rest.param.UpdateBizConfigParam;

@Service
@Transactional(rollbackFor={Exception.class})
public class BizConfigServiceImpl implements BizConfigService, ApplicationEventPublisherAware {
  
  @Autowired
  private FdfsService fdfsService;
  
  @Autowired
  private BizConfigRepository bizConfigRepo;
  
  private ApplicationEventPublisher publisher;

  @Override
  public BizConfig createBizConfig(CreateBizConfigParam param) throws Exception {
    BizConfig config = new BizConfig();
    BeanUtils.copyProperties(param, config);
    if (param.getImg() != null && !param.getImg().isEmpty()) {
      config.setImgUrl(fdfsService.getFileUrl(fdfsService.uploadToFastDFS(param.getImg().getOriginalFilename(), param.getImg().getBytes())));
    }
    if (param.getAudio() != null && !param.getAudio().isEmpty()) {
      config.setAudioUrl(fdfsService.getFileUrl(fdfsService.uploadToFastDFS(param.getAudio().getOriginalFilename(), param.getAudio().getBytes())));
    }
    return bizConfigRepo.saveAndFlush(config);
  }

  @Override
  public BizConfig updateBizConfig(UpdateBizConfigParam param) throws Exception {
    BizConfig existed = bizConfigRepo.findOne(param.getConfigId());
    BeanPropertyUtils.copyProperties(param, existed);
    if (param.getImg() != null && !param.getImg().isEmpty()) {
      existed.setImgUrl(fdfsService.getFileUrl(fdfsService.uploadToFastDFS(param.getImg().getOriginalFilename(), param.getImg().getBytes())));
    }
    if (param.getAudio() != null && !param.getAudio().isEmpty()) {
      existed.setAudioUrl(fdfsService.getFileUrl(fdfsService.uploadToFastDFS(param.getAudio().getOriginalFilename(), param.getAudio().getBytes())));
    }
    BizConfig updated = bizConfigRepo.saveAndFlush(existed);
    publisher.publishEvent(new BizConfigUpdateEvent(this, updated));
    return updated;
  }

  @Override
  public BizConfig findConfig(QueryBizConfigParam param) {
    if (StringUtils.hasText(param.getMeetingToken())) {
      return bizConfigRepo.findByMeetingToken(param.getMeetingToken());
    }
    if (StringUtils.hasText(param.getDeviceSn())) {
      return bizConfigRepo.findByDeviceSn(param.getDeviceSn());
    }
    return null;
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.publisher = applicationEventPublisher;
  }

}
