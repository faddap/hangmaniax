package hm;

import hm_model.Game;
import hm_model.JsonRPCResponse;
import hm_model.PMF;
import hm_model.User;
import hm_model.JsonRPCResponse.ErrorCode;
import hm_model.Word;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
//import com.google.appengine.api.datastore.Query;
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
			jsonResp = JsonRPCResponse.buildErrorResponse(ErrorCode.METHOD_NOT_FOUND, "Method not found!");
			
			JSONObject jsonReq = new JSONObject(reqStr);
			JSONObject jsonParams = jsonReq.has("params") ? jsonReq.optJSONObject("params") : null;
			
			if (jsonReq.has("method") && "login".equals(jsonReq.optString("method"))) {
				PersistenceManager pm = PMF.get().getPersistenceManager();
				
				//extract parameters
				String email = jsonParams.optString("email");
				String pass = jsonParams.optString("password");
				
				//fetch user from datastore
				try {
					User loggedIn = pm.getObjectById(User.class, email);
					if (loggedIn.getPass().equals(pass)) {
						HttpSession session = req.getSession();
						session.setMaxInactiveInterval(10*60);
						session.setAttribute("email", loggedIn.getEmail());
						session.setAttribute("name", loggedIn.getName());
						session.setAttribute("score", loggedIn.getScore());
						jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true, 'name': '"+session.getAttribute("name")+"', 'score': '"+session.getAttribute("score")+"'}");
					} else {
						jsonResp = JsonRPCResponse.buildErrorResponse(1, "Wrong password!");
					}
				} catch(JDOException e) {
					jsonResp = JsonRPCResponse.buildErrorResponse(0, e.getMessage());
				} finally {
					pm.close();
				}
				
			} else if (jsonReq.has("method") && "signup".equals(jsonReq.optString("method"))) {
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
					jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true, 'method': 'signup'}");
				} finally {
					pm.close();
				}
			} else if (jsonReq.has("method") && "logout".equals(jsonReq.optString("method"))) {
				req.getSession().invalidate();
				jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true}");
			} else if (jsonReq.has("method") && "checkSession".equals(jsonReq.optString("method"))) {
				HttpSession session = req.getSession();
				if (session != null && session.getAttribute("name") != null && session.getAttribute("score") != null) {
					jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true, 'name': '"+session.getAttribute("name")+"', 'score': '"+session.getAttribute("score")+"'}");
				} else {
					jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': false}");
				}
			} else if (jsonReq.has("method") && "startGame".equals(jsonReq.optString("method"))) {
				HttpSession session = req.getSession();
				if (session.getAttribute("email") != null) {
					//TODO: Draw random word
					String email = (String) session.getAttribute("email");
					Word word = new Word("occurrence");
					Game game = new Game(email, word);
					jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true, 'length': 10}");
					session.setAttribute("game", game);
				} else {
					jsonResp = JsonRPCResponse.buildErrorResponse(ErrorCode.SERVER_ERROR, "Invalid session!");
				}
			} else if (jsonReq.has("method") && "letterSubmit".equals(jsonReq.optString("method"))) {
				
				//TODO: obtain letter and and give it to the game instance
				HttpSession session = req.getSession();
				Game game = (Game) session.getAttribute("game");
				System.out.print(session);
				jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true}");
				
				//TODO: increment word's <code>played</code> field
				if (game.isWin() || game.isLoss()) {
					PersistenceManager pm = PMF.get().getPersistenceManager();
					//TODO: increment word's <code>guessed</code> field
					String playerEmail = game.getEmail();
					try {
						User player = pm.getObjectById(User.class, playerEmail);
						if (game.isWin()) {
							player.addPoints(game.getPointsForTheGame());
						} else {
							//TODO: act accordingly
						}
					} catch (JDOException e) {
						jsonResp = JsonRPCResponse.buildErrorResponse(0, e.getMessage());
					} finally {
						pm.close();
					}
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