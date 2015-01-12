package org.javaprotrepticon.android.fitnessfactsdiary.sync;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.javaprotrepticon.android.fitnessfactsdiary.R;
import org.javaprotrepticon.android.fitnessfactsdiary.account.Authenticator;
import org.javaprotrepticon.android.fitnessfactsdiary.data.SecuredDataStorage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SyncResult;
import android.os.Bundle;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    
	public static final String ACTION_SYNC = "ru.magnat.smnavigator.sync.ACTION_SYNC";
	
    @SuppressWarnings("unused")
	private ContentResolver mContentResolver;
    
    @SuppressWarnings("unused")
	private SecuredDataStorage mDataStorage;
    private Account mAccount;
    
    private String sessionToken;
    private SSLContext sslContext;
    private HostnameVerifier hostnameVerifier = new HostnameVerifier () {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
		
	};
    
	@SuppressWarnings("unused")
	private HttpsURLConnection prepareConnection(String serviceName) throws Exception {
		URL url = new URL(getContext().getResources().getString(R.string.syncServerSecure) + serviceName + "?token=" + sessionToken);
		 
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection(); 
		httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
		httpsURLConnection.setHostnameVerifier(hostnameVerifier); 
		httpsURLConnection.addRequestProperty("token", sessionToken); 
		
		return httpsURLConnection;
	}
	
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }
    
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        
        mContentResolver = context.getContentResolver();
    }
    
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    	mAccount = account;    	
    	
    	sendNotification("started");
    	
    	Authenticator.validateSession(getContext(), account);
		
    	sessionToken = AccountManager.get(getContext()).peekAuthToken(account, account.type);
    	
    	Timer timer = new Timer("ackSender");
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendNotification("ack");
			}
		}, 0, 1500);
    	
		try {
			TimeUnit.SECONDS.sleep(2);
		
			mDataStorage = SecuredDataStorage.get(getContext(), account);
			
			SecuredDataStorage.close();
			
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			e.printStackTrace();
			sendNotification("error");	
	    }
		
		timer.cancel();
		
		saveLastSync();
		
		sendNotification("completed");
    }
    
    private void saveLastSync() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(mAccount.name + ".global", Context.MODE_MULTI_PROCESS);
        
        Editor editor = sharedPreferences.edit();
        editor.putString("lastSync", new Timestamp(System.currentTimeMillis()).toString());
        editor.commit();
    }
    
    private void sendNotification(String action) {
    	Intent intentStarted = new Intent(ACTION_SYNC);
    	intentStarted.putExtra("action", action);
    	intentStarted.putExtra("account", mAccount.name);
    	
    	getContext().sendBroadcast(intentStarted);
    }
    
	@Override
	public void onSyncCanceled() {
		super.onSyncCanceled();
		
		sendNotification("canceled"); 
	}

	@Override
	public void onSyncCanceled(Thread thread) {
		super.onSyncCanceled(thread);
		
		sendNotification("canceled"); 
	}

}
