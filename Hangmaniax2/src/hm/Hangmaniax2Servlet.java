package hm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

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
				jsonResp.put("credentials", jsonParams);
			} else if (jsonReq.has("method") && "signup".equals(jsonReq.optString("method"))) {
				jsonResp = new JSONObject("{'success': true}");
				jsonResp.put("credentials", jsonParams);
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
