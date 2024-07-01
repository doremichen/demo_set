package com.adam.app.demoset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.adam.app.demoset.binder.DemoBinderAct;
import com.adam.app.demoset.binder.IMyAidlInterface;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class DemoBinderActTest {

    private DemoBinderAct activity;
    private IBinder mockBinder;
    private IMyAidlInterface mockAidlInterface;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(DemoBinderAct.class)
                .create()
                .start()
                .resume()
                .get();

        mockBinder = mock(IBinder.class);
        mockAidlInterface = mock(IMyAidlInterface.class);
    }

    @Test
    public void testOnCreate() {
        assertNotNull(activity.findViewById(R.id.et_input_a));
        assertNotNull(activity.findViewById(R.id.et_input_b));
        assertNotNull(activity.findViewById(R.id.tv_output_c));
    }

    @Test
    public void testExecuteAidlBinderCall() throws Exception {
        // Setup
        EditText etInputA = activity.findViewById(R.id.et_input_a);
        EditText etInputB = activity.findViewById(R.id.et_input_b);
        TextView tvOutputC = activity.findViewById(R.id.tv_output_c);

        etInputA.setText("5");
        etInputB.setText("10");

        Field proxyAidl = DemoBinderAct.class.getDeclaredField("mProxyAidl");
        proxyAidl.setAccessible(true);
        proxyAidl.set(activity, mockAidlInterface);

        // Call method
        activity.onExecuteBinderCall(tvOutputC);

        // Verify
        verify(mockAidlInterface).add(5, 10);
    }

    @Test
    public void testExecuteMessengerBinderCall() throws Exception {
        // Setup
        Field isMessenger = DemoBinderAct.class.getDeclaredField("isMessenger");
        isMessenger.setAccessible(true);
        isMessenger.set(activity, true);
        EditText etInputA = activity.findViewById(R.id.et_input_a);
        EditText etInputB = activity.findViewById(R.id.et_input_b);
        etInputA.setText("5");
        etInputB.setText("10");

        Messenger mockMessenger = mock(Messenger.class);
        Field messenger = DemoBinderAct.class.getDeclaredField("mMessenger");
        messenger.setAccessible(true);
        messenger.set(activity, mockMessenger);

        // Call method
        activity.onExecuteBinderCall(new View(activity));

        // Verify
        verify(mockMessenger).send(any(Message.class));
    }

    @Test
    public void testShowResult() {
        // Setup
        TextView tvOutputC = activity.findViewById(R.id.tv_output_c);

        // Call method
        activity.showResult(15);

        // Verify
        assertEquals("c: 15", tvOutputC.getText().toString());
    }
}