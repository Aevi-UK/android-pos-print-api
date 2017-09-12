package com.aevi.print;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowPackageManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

@Config(sdk = Build.VERSION_CODES.LOLLIPOP, manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class PrintManagerImplTest {

    private static final String PRINT_SERVICE_PACKAGE = "com.aevi.print.service";
    private static final String PRINT_MESSENGER_SERVICE_CLASS = "com.aevi.print.service.PrinterMessagingService";

    private PrinterManager printerManager;

    @Before
    public void setup() {
        ShadowLog.stream = System.out;
        initMocks(this);
    }

    @Test
    public void checkIsPrintManagerInstalledFlagReportsOk() throws RemoteException {
        setupMockBoundMessengerService();
        setupProcessingService();

        assertThat(printerManager.isPrinterServiceAvailable()).isTrue();
    }

    @Test
    public void checkIsPrintManagerInstalledFlagReportsNotInstalled() throws RemoteException {
        setupProcessingService();

        assertThat(printerManager.isPrinterServiceAvailable()).isFalse();
    }

    private void setupMockBoundMessengerService() {
        ShadowApplication shadowApplication = ShadowApplication.getInstance();
        MockMessageService mockMessageService = new MockMessageService();

        shadowApplication.setComponentNameAndServiceForBindService(new ComponentName(PRINT_SERVICE_PACKAGE, PRINT_MESSENGER_SERVICE_CLASS), mockMessageService.onBind(null));

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(PRINT_SERVICE_PACKAGE, PRINT_MESSENGER_SERVICE_CLASS));

        ShadowPackageManager shadowPackageManager = Shadows.shadowOf(RuntimeEnvironment.application.getPackageManager());
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.serviceInfo = new ServiceInfo();
        shadowPackageManager.addResolveInfoForIntent(intent, resolveInfo);
    }

    private void setupProcessingService() throws RemoteException {
        printerManager = new PrinterManagerImpl(RuntimeEnvironment.application);
    }

    private class MockMessageService extends Service {

        List<Message> messages = new ArrayList<>();

        class IncomingHandler extends Handler {

            @Override
            public void handleMessage(Message msg) {
                messages.add(msg);
            }
        }

        private final Messenger incomingMessenger = mock(Messenger.class);

        @Override
        public IBinder onBind(Intent intent) {
            return incomingMessenger.getBinder();
        }
    }

}
