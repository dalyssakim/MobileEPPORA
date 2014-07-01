package com.eps.controller;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ederbase.model.EbDatabase;
import com.eps.model.EbUserData;

public class EpsListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent event) {

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		EbUserData client = (EbUserData) event.getSession().getAttribute(
				"client");
		if (client != null) {
			EbDatabase dbDyn = new EbDatabase("ebeps");
			dbDyn.ExecuteUpdate("UPDATE X25User SET SuccessLoginTime=0 WHERE RecId="
					+ client.getNmUserId());
		}
		event.getSession().removeAttribute("client");
	}

}
