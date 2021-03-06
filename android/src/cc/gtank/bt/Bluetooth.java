package cc.gtank.bt;

import android.content.Context;

public interface Bluetooth {
    public void setContext(Context appContext);
    public boolean obtainProxy();
    public void releaseProxy() throws Exception;
    public void startVoiceRecognition();
    public void stopVoiceRecognition();
    public boolean isEnabled();
    public boolean isAvailable();
    
    public static final String BLUETOOTH_STATE = "cc.gtank.bt.BLUETOOTH_STATE";
}
