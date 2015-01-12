package org.javaprotrepticon.android.fitnessfactsdiary.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import org.javaprotrepticon.android.utils.Apps;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;

public class SecuredDataStorage {
	
	private static final String TAG = "DB_HELPER_SECURED";
	
	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static SecuredDataStorage sInstance;
	
	private JdbcPooledConnectionSource mConnectionSource;
	 
	private SecuredDataStorage(Context context, Account account) {
		AccountManager accountManager = AccountManager.get(context); 
		
		String accountName = account.name;
		String accountPassword = accountManager.getPassword(account);
		
		String accountFolder = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + accountName + "/";
		String accountStorage = accountFolder + context.getPackageName() + "-" + Apps.getVersionName(context);
		
		File accountDirectory = new File(accountFolder);
		
		if (!accountDirectory.exists()) accountDirectory.mkdirs();
		if (!new File(accountFolder + "init.sql").exists()) {
			copyInitialScript(context, accountFolder);
		}
		
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:h2:file:");
		urlBuilder.append(accountStorage + ";");
		urlBuilder.append("database_to_upper=false;");
		urlBuilder.append("file_lock=no;");
		urlBuilder.append("ifexists=false;");
		urlBuilder.append("ignorecase=true;");
		urlBuilder.append("page_size=1024;");
		urlBuilder.append("cache_size=1024;");
		urlBuilder.append("autocommit=on;");
		urlBuilder.append("compress=false;");
		urlBuilder.append("cipher=aes;");
		urlBuilder.append("user=" + accountName + ";");
		urlBuilder.append("password=" + accountPassword + " " + accountPassword + ";");
		
		if (!new File(accountStorage + ".h2.db").exists()) {	
			urlBuilder.append("init=runscript from '" + accountFolder + "init.sql" + "';");
		} else {
			urlBuilder.append("init=set schema fitnessfactsdiary;");
		}
		
		String url = urlBuilder.toString();
		
		try {
			mConnectionSource = new JdbcPooledConnectionSource(url, accountName, accountPassword + " " + accountPassword); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void copyInitialScript(Context context, String accountFolder) {
		try {
			InputStream is = context.getAssets().open("init.sql");
			OutputStream os = new FileOutputStream(accountFolder + "init.sql"); 
			
			byte[] buffer = new byte[1024];
			int count = -1;
			
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static SecuredDataStorage get(Context context, Account account) {
		if (sInstance == null) {
			sInstance = new SecuredDataStorage(context, account);
		}
		
		Log.d(TAG, "storage:instantiate->ok");
		
		return sInstance;
	}
	
	public synchronized static void close() {
		if (sInstance != null) {
			try {
				sInstance.mConnectionSource.close();
				sInstance = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
