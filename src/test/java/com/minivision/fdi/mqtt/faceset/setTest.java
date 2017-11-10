package com.minivision.fdi.mqtt.faceset;

import com.minivision.fdi.common.ImageUtils;
import com.minivision.fdi.faceplat.client.FacePlatClient;
import com.minivision.fdi.faceplat.result.detect.faceset.SetListResult;
import com.minivision.fdi.service.MeetService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class setTest {

    @Autowired
    private FacePlatClient client;

    @Autowired MeetService meetService;

    @Test
    public void test(){

        //byte[] image = FileUtils.readFileToByteArray(new File("E://44.jpg"));
        //SearchResult search = client.search("d4f8f338-a751-4875-b044-a521712f5b03", image, 100);
        //CompareResult result = client.compare("c52c68a5-23c2-4ac8-b07d-8b1b4e5347e91", "f7426a29-bd7b-450d-ad5c-637da4cf94b7");
        SetListResult faceList = client.faceList(0, 2);
        System.out.println(ToStringBuilder.reflectionToString(faceList));
    }
    public static void main(String[] args) throws IOException {
        File file = new File("D://a//abc.jpg");
        BufferedImage read = ImageIO.read(file);
        BufferedImage subImage =  ImageUtils.enlarge(read,200,100, 600,500);
        byte[] bytes = ImageUtils.writeImageToBytes(subImage, "png");
        String path = UUID.randomUUID().toString();
        FileUtils.writeByteArrayToFile(new File("D://a//"+path+".jpg"),bytes);
    }

}
