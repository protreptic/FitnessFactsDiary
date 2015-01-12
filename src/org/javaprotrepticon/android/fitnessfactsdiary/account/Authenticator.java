package org.javaprotrepticon.android.fitnessfactsdiary.account;

import java.sql.Timestamp;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Authenticator extends AbstractAccountAuthenticator {
	
	private Context mContext;
	
	public Authenticator(Context context) {
		super(context);
		
		mContext = context;
	}

	public static void validateSession(Context context, Account account) {
		AccountManager accountManager = AccountManager.get(context);
		
		String sessionToken = accountManager.peekAuthToken(account, AccountWrapper.ACCOUNT_TYPE);
		String sessionExpires = accountManager.getUserData(account, "sessionExpiration");
		
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		Timestamp sessionTimestamp = Timestamp.valueOf(sessionExpires);
		
		if (currentTimestamp.after(sessionTimestamp)) {
			accountManager.invalidateAuthToken(account.type, sessionToken); 
			accountManager.clearPassword(account);
		}
	}
	
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(mContext, SignInActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
         
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        
        return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        if (!authTokenType.equals(AccountWrapper.ACCOUNT_TYPE)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_CODE, "invalid authTokenType");
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            
            return result;
        }

        validateSession(mContext, account); 
        
        AccountManager accountManager = AccountManager.get(mContext);
        
		String accountName = account.name;
		String accountPassword = accountManager.getPassword(account);
		String accountType = account.type;
		String accountToken = null;
        String accountExpiration = null;
		
        if (accountPassword != null) {
			accountToken = "ijfsiodjfsdfjs9df0sdjf9sdfosdf";
			accountExpiration = null;
    		
			accountManager.setAuthToken(account, account.type, accountExpiration); 
			accountManager.setUserData(account, "sessionExpiration", accountExpiration); 
    		
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            result.putString(AccountManager.KEY_AUTHTOKEN, accountToken);

            return result;
        }
        
        Intent intent = new Intent(mContext, SignInActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        
        return bundle;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle bundle) throws NetworkErrorException {
		return null;
	}
	
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
	
}