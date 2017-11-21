package com.minivision.fdi.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.minivision.fdi.entity.Face;
import org.springframework.data.jpa.repository.Query;

public interface FaceRepository extends JpaRepository<Face,String> ,
    JpaSpecificationExecutor<Face> {
    int deleteByIdIn(List<String> delTokens);

    List<Face> findByIdIn(Collection<String> ids);

    Face findFaceByFaceToken(String s);

    Face findByQrCodeAndMeetingToken(String qrCode,String meeting);

    Face findByFaceToken(String faceToken);
    
    int countByMeetingToken(String meetingToken);

    @Query(value = "select count(*) from face where face.meeting_id =?1 and face.sign_in =?2",nativeQuery = true)
    int countSignIn(String meetToken,boolean signIn);
}
