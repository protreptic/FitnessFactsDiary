package org.javaprotrepticon.android.fitnessfactsdiary;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ACRAConfigurationException;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.javaprotrepticon.android.utils.Device;
import org.javaprotrepticon.android.utils.Network;

import android.app.Application;

@ReportsCrashes(formKey = "", resToastText = R.string.crashMessage) 
public class FitnessFactsDiary extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initAcra();
	}
	
	private void initAcra() {
        ACRA.init(this);
        
		ACRAConfiguration configuration = ACRA.getConfig();
		
		try {
			configuration.setFormUri("http://localhost/"); 
			configuration.setDisableSSLCertValidation(true); 
			configuration.setSocketTimeout(5000); 
			configuration.setConnectionTimeout(5000); 
			
			configuration.setMode(ReportingInteractionMode.TOAST);
		} catch (ACRAConfigurationException e) {
			e.printStackTrace();
		}
        
        ACRA.setConfig(configuration); 
        
        ACRA.getErrorReporter().putCustomData("device_id", Device.getDeviceId(this));
        ACRA.getErrorReporter().putCustomData("mac_wlan0", Network.getMACAddress("wlan0"));
        ACRA.getErrorReporter().putCustomData("mac_eth0", Network.getMACAddress("eth0"));
        ACRA.getErrorReporter().putCustomData("ip_v4", Network.getIPAddress(true));
        ACRA.getErrorReporter().putCustomData("ip_v6", Network.getIPAddress(false));
	}
	
}
