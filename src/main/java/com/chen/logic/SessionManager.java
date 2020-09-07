package com.chen.logic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap; 
import io.netty.util.AttributeKey;

public class SessionManager {

	private static AttributeKey<UserSession> attributeKey = AttributeKey.valueOf("user_session");

	private static ConcurrentMap<String, UserSession> userId2Session = new ConcurrentHashMap<>();

	public static UserSession getSessionBy(String userId) {
		UserSession session = userId2Session.get(userId);
		return session;
	}

	public static void registerSession(UserSession session) {
		String userId = session.getUser().getUser_id();
		userId2Session.put(userId, session);
		session.getChannel().attr(attributeKey).set(session); 
	}

	public static void removeSession(String userId) {
		UserSession session = userId2Session.get(userId);
		if (session != null) {
			session.getChannel().attr(attributeKey).set(null);
			session.getChannel().close();
			userId2Session.remove(userId);
		}
	}

}
