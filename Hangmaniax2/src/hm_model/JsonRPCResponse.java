package hm_model;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class JsonRPCResponse {
	public static enum ErrorCode {
		PARSE_ERROR,
		INVALID_REQUEST,
		METHOD_NOT_FOUND,
		INVALID_PARAMS,
		INTERNAL_ERROR,
		SERVER_ERROR
	}
	
	private static long getIntErrorCode(ErrorCode e) {
		long code = -32000;
		switch(e) {
			case PARSE_ERROR: code = -32700; break;
			case INVALID_REQUEST: code = -32600; break;
			case METHOD_NOT_FOUND: code = -32601; break;
			case INVALID_PARAMS: code = -32602; break;
			case INTERNAL_ERROR: code = -32603; break;
			default: code = -32000;
		}
		return code;
	}
	
	public static JSONObject buildSuccessResponse(JSONObject result) throws JSONException {
		JSONObject resp = new JSONObject();
		resp.put("result", result);
		return resp;
	}
	public static JSONObject buildSuccessResponse(String result) throws JSONException {
		JSONObject resp = new JSONObject();
		resp.put("result", new JSONObject(result));
		return resp;
	}
	public static JSONObject buildErrorResponse(ErrorCode code, String message) throws JSONException {
		JSONObject resp = new JSONObject();
		
		JSONObject err = new JSONObject();
		err.put("code", getIntErrorCode(code));
		err.put("message", message);
		
		resp.put("error", err);
		return resp;
	}
	public static JSONObject buildErrorResponse(long code, String message) throws JSONException {
		JSONObject resp = new JSONObject();
		
		JSONObject err = new JSONObject();
		err.put("code", code);
		err.put("message", message);
		
		resp.put("error", err);
		return resp;
	}
}
