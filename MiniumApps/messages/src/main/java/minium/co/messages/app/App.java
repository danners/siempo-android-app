package minium.co.messages.app;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.Trace;

import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.messages.data.Contact;
import minium.co.messages.data.Conversation;

/**
 * Concrete implementation of {@link CoreApplication}
 * This class will implement specific behaviors that differs with core library
 *
 * Created by shahab on 3/17/16.
 */
@EApplication
public class App extends CoreApplication {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "App";

    @Trace(tag = TRACE_TAG)
    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Application Id: " + minium.co.messages.BuildConfig.APPLICATION_ID
                + " || Version code: " + minium.co.messages.BuildConfig.VERSION_CODE
                + " || Version name: " + minium.co.messages.BuildConfig.VERSION_NAME
                + " || Git Sha: " + BuildConfig.GIT_SHA
                + " || Build time:  " + BuildConfig.BUILD_TIME
                + " || Build flavor: " + minium.co.messages.BuildConfig.FLAVOR
                + " || Build type: " + minium.co.messages.BuildConfig.BUILD_TYPE);

        Contact.init(this);
        Conversation.init(this);
    }
}
