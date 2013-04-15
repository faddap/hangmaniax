package hm;

import hm_model.Game;
import hm_model.JsonRPCResponse;
import hm_model.PMF;
import hm_model.TurnOutcome;
import hm_model.User;
import hm_model.JsonRPCResponse.ErrorCode;
import hm_model.Word;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
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
			} else if (jsonReq.has("method") && "getActiveUser".equals(jsonReq.optString("method"))) {
				HttpSession session = req.getSession();
				
				if (this.checkSession(session)) {
					jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true, 'name': '"+session.getAttribute("name")+"', 'score': '"+session.getAttribute("score")+"'}");
				} else {
					jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': false}");
				}
			} else if (jsonReq.has("method") && "startGame".equals(jsonReq.optString("method"))) {
				HttpSession session = req.getSession();
				if (session.getAttribute("email") != null) {
					PersistenceManager pm = PMF.get().getPersistenceManager();
					String email = (String) session.getAttribute("email");
					Word word = Word.getRandom(pm);
					Game game = new Game(email, word);
					jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': true, 'length': "+word.toString().length()+"}");
					session.setAttribute("game", game);
				} else {
					jsonResp = JsonRPCResponse.buildErrorResponse(ErrorCode.SERVER_ERROR, "No user logged in!");
				}
			} else if (jsonReq.has("method") && "letterSubmit".equals(jsonReq.optString("method"))) {
				HttpSession session = req.getSession();
				if (this.checkSession(session)) {
					PersistenceManager pm = null;
					char letter = jsonParams.optString("letter").charAt(0);
					Game game = (Game) session.getAttribute("game");
					TurnOutcome outcome = game.turn(letter);
					JSONObject jsonOutcome = outcome.toJson();
					
					if (game.isWin() || game.isLoss()) {
						try {
							// We already need the word, so fetch it
							pm = PMF.get().getPersistenceManager();
							Word w = pm.getObjectById(Word.class, game.getWord());
							
							// Increment word's <code>played</code> field no matter of the result
							w.justPlayed();
							
							//TODO: persist word (and user)
							if (game.isWin()) {
								// We already need the player as well
								User player = pm.getObjectById(User.class, game.getEmail());
								player.addPoints(game.getPointsForTheGame());
								w.justGuessed();
								
								//Build success response
								
								jsonOutcome.put("gameStatus", "win");
							} else if (game.isLoss()) {
								jsonOutcome.put("gameStatus", "loss");
								
								//TODO: grant points for asker, if there is one
							}
							
							jsonResp = JsonRPCResponse.buildSuccessResponse(jsonOutcome);
						} catch (JDOException e) {
							jsonResp = JsonRPCResponse.buildErrorResponse(0, e.getMessage());
						} finally {
							pm.close();
						}
					} else {
						jsonOutcome.put("gameStatus", "in progress");
					}
					
					if (jsonOutcome.has("error") && jsonOutcome.optBoolean("error")) {
						jsonResp = JsonRPCResponse.buildErrorResponse(0, "This letter has been already entered!");
					} else {
						jsonResp = JsonRPCResponse.buildSuccessResponse(jsonOutcome);
					}
				} else {
					jsonResp = JsonRPCResponse.buildErrorResponse(ErrorCode.SERVER_ERROR, "No user logged in!");
				}
			} else if (jsonReq.has("method") && "wordSubmit".equals(jsonReq.optString("method"))) {
				String word = jsonParams.optString("word");
				if (word != null && !"".equals(word)) {
					PersistenceManager pm = PMF.get().getPersistenceManager();
					Word w = new Word(word);
					try {
						pm.makePersistent(w);
						jsonResp = JsonRPCResponse.buildSuccessResponse("{'success': 'true', 'id': "+w.getId()+"}");
					} catch(JDOException e) {
						jsonResp = JsonRPCResponse.buildErrorResponse(0, e.getMessage());
					} finally {
						pm.close();
					}
				} else {
					jsonResp = JsonRPCResponse.buildErrorResponse(0, "Word incorrect!");
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
	
	private boolean checkSession(HttpSession session) {
		return session != null && session.getAttribute("name") != null && session.getAttribute("score") != null;
	}
}