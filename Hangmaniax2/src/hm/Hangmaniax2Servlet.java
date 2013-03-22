package hm;

import hm_model.PMF;
import hm_model.User;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class Hangmaniax2Servlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.getWriter().write("hello");
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String reqStr = req.getParameter("jsonRPC");
		
		PrintWriter wr = null;
		try {
			wr = resp.getWriter();
			JSONObject jsonResp;
			jsonResp = new JSONObject("{'error': 'Invalid input method or params'}");
			
			JSONObject jsonReq = new JSONObject(reqStr);
			JSONObject jsonParams = jsonReq.has("params") ? jsonReq.optJSONObject("params") : null;
			if (jsonReq.has("method") && "login".equals(jsonReq.optString("method"))) {
				jsonResp = new JSONObject("{'success': true}");
				PersistenceManager pm = PMF.get().getPersistenceManager();
				
				//extract parameters
				String email = jsonParams.optString("email");
				String pass = jsonParams.optString("password");
				
				//fetch user from datastore
				User loggedIn = pm.getObjectById(User.class, email);
				System.out.println(loggedIn.getName());
			} else if (jsonReq.has("method") && "signup".equals(jsonReq.optString("method"))) {
				jsonResp = new JSONObject("{'success': true}");
				PersistenceManager pm = PMF.get().getPersistenceManager();
				
				//extract parameters
				String username = jsonParams.optString("username");
				String email = jsonParams.optString("email");
				String pass = jsonParams.optString("password");
				
				//create key
				Key key = KeyFactory.createKey(User.class.getSimpleName(), email);
				String encodedKey = KeyFactory.keyToString(key);
				
				//create user and set his key
				User me = new User(username, email, pass);
				me.setKey(encodedKey);
				me.setRole(User.Role.ADMIN);
				
				//save user
				try {
					pm.makePersistent(me);
				} finally {
					pm.close();
				}
			}
			
			wr.write(jsonResp.toString());
		} catch(JSONException e) {
			e.printStackTrace();
		} finally {
			wr.flush();
			wr.close();
		}
	}
}
