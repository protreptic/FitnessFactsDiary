package org.javaprotrepticon.android.fitnessfactsdiary.account;

public class AccountWrapper {
	
    public static final String ACCOUNT_AUTHORITY = "org.javaprotrepticon.android.fitnessfactsdiary";
    public static final String ACCOUNT_TYPE = "org.javaprotrepticon.android.fitnessfactsdiary";
	
	private String token;
	private String expiration;
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	
	@Override
	public String toString() {
		return "AccountWrapper [token=" + token + ", expiration=" + expiration + "]";
	}
	
}