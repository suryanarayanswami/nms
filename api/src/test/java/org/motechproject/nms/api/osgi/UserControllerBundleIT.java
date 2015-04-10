package org.motechproject.nms.api.osgi;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.nms.kilkari.domain.Subscriber;
import org.motechproject.nms.kilkari.domain.SubscriptionPack;
import org.motechproject.nms.kilkari.repository.SubscriberDataService;
import org.motechproject.nms.kilkari.repository.SubscriptionPackDataService;
import org.motechproject.nms.kilkari.service.KilkariService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.http.SimpleHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;


/**
 * Verify that LanguageService HTTP service is present and functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class UserControllerBundleIT extends BasePaxIT {
    private static final String ADMIN_USERNAME = "motech";
    private static final String ADMIN_PASSWORD = "motech";

    @Inject
    private KilkariService kilkariService;
    @Inject
    private SubscriberDataService subscriberDataService;
    @Inject
    private SubscriptionPackDataService subscriptionPackDataService;

    private void setupData() {
        subscriptionPackDataService.deleteAll();
        SubscriptionPack pack1 = subscriptionPackDataService.create(new SubscriptionPack("pack1"));
        SubscriptionPack pack2 = subscriptionPackDataService.create(new SubscriptionPack("pack2"));
        List<SubscriptionPack> onePack = Arrays.asList(pack1);
        List<SubscriptionPack> twoPacks = Arrays.asList(pack1, pack2);

        subscriberDataService.deleteAll();
        Subscriber subscriber1 = subscriberDataService.create(new Subscriber("0000000000", onePack));
        Subscriber subscriber2 = subscriberDataService.create(new Subscriber("0000000001", twoPacks));
    }

    @Test
    public void testUserRequest() throws IOException, InterruptedException {
        setupData();
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/api/kilkari/user?callingNumber=0000000001&operator=OP&circle=AA&callId=0123456789abcde", TestContext.getJettyPort()));

        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes())));

        assertTrue(SimpleHttpClient.execHttpRequest(httpGet, "{\"languageLocationCode\":null,\"defaultLanguageLocationCode\":null,\"subscriptionPackList\":[\"pack2\",\"pack1\"]}"));
    }

    @Test
    public void testInvalidServiceName() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/api/NO_SERVICE/user?callingNumber=0123456789&operator=OP&circle=AA&callId=0123456789abcde", TestContext.getJettyPort()));

        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes())));

        //todo: replace with execHttpRequest method that also tests response body (in addition to status code)
        //todo: when it's available in platform: org.motechproject.testing.osgi.http.SimpleHttpClient
        SimpleHttpClient.execHttpRequest(httpGet, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    @Ignore
    public void testNoCallingNumber() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/api/kilkari/user?operator=OP&circle=AA&callId=0123456789abcde", TestContext.getJettyPort()));

        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes())));

        SimpleHttpClient.execHttpRequest(httpGet, "{\"failureReason\":\"<callingNumber: Not Present>\"}");
    }

    @Test
    @Ignore
    public void testInvalidCallingNumber() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/api/kilkari/user?callingNumber=XXXXXXX&operator=OP&circle=AA&callId=0123456789abcde", TestContext.getJettyPort()));

        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes())));

        SimpleHttpClient.execHttpRequest(httpGet, "{\"failureReason\":\"<callingNumber: Invalid>\"}");
    }

    @Test
    @Ignore
    public void testNoOperator() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/api/kilkari/user?callingNumber=XXXXXXX&circle=AA&callId=0123456789abcde", TestContext.getJettyPort()));

        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes())));

        SimpleHttpClient.execHttpRequest(httpGet, "{\"failureReason\":\"<operator: Not Present>\"}");
    }

    @Test
    @Ignore
    public void testNoCircle() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/api/kilkari/user?callingNumber=XXXXXXX&operator=OP&callId=0123456789abcde", TestContext.getJettyPort()));

        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes())));

        SimpleHttpClient.execHttpRequest(httpGet, "{\"failureReason\":\"<callId: Not Present>\"}");
    }

    @Test
    @Ignore
    public void testNoCallId() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/api/kilkari/user?callingNumber=XXXXXXX&operator=OP&circle=AA", TestContext.getJettyPort()));

        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((ADMIN_USERNAME + ":" + ADMIN_PASSWORD).getBytes())));

        SimpleHttpClient.execHttpRequest(httpGet, "{\"failureReason\":\"<callId: Not Present>\"}");
    }
}
