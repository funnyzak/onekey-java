package com.github.funnyzak.onekey.biz.service.resource;

import com.github.funnyzak.onekey.biz.bean.CloudStorageObject;
import com.github.funnyzak.onekey.biz.config.tencent.cos.TenCosService;
import com.github.funnyzak.onekey.biz.config.upload.FileUploadManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

/**
 * ResourceService Tester.
 *
 * @author Potato
 * @version 1.0
 * @since <pre>12月 17, 2019</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceServiceTest {

    @Autowired
    ResourceService resourceService;

    @Autowired
    TenCosService cosFileManager;

    @Autowired
    FileUploadManager fileUploadManager;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCosUpload() throws Exception {
        String filePath = "/Users/potato/Work/Project/Collection-System/tempdir/upload/20191030/cf5286a6b94e48368e1a1ac8642050ae.jpg";
        CloudStorageObject cosR = cosFileManager.upload(filePath);
        Assert.assertTrue("成功上传", cosR != null);
    }

    @Test
    public void testPreSignedUrl() throws Exception {
        URL url = cosFileManager.signedUrl("membersystem/potato233/20191217/9ab42635e90d452c8421c9e972dd879c.jpg");
        Assert.assertTrue("成功上传", url != null);
    }
} 
