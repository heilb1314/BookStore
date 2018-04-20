package model;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public final class SessionAttributeManager {

	private SessionAttributeManager() {
	}
	
	private static final String SUCCESS_MESSAGE_ID = "successMessage";
    private static final String ERROR_MESSAGE_ID = "errorMessage";
    private static final String CARRY_FORWARD_ATTRIBUTES_ID = "carryForwardAttribute";
    
    /**
     * Transfer carry forward session attributes to request scope
     * 
     * @param request
     */
    public static void transferAttributesToRequestScope(HttpServletRequest request) {
    		Map<String, Object> attributes = getCarryForwardAttributes(request);
    		if(attributes != null) {
    			// set possible request attributes
    	        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
    	            request.setAttribute(entry.getKey(), entry.getValue());
    	        }
    		}
    }
    
    /**
     * Get the carry forward attributes
     * @param request
     * @return
     */
    public static Map<String, Object> getCarryForwardAttributes(HttpServletRequest request) {
	    	@SuppressWarnings("unchecked")
			Map<String, Object> attributes = (Map<String, Object>) request.getSession().getAttribute(CARRY_FORWARD_ATTRIBUTES_ID);
		if(attributes == null) {
			attributes = new HashMap<>();
		}
		return attributes;
    }
    
    /**
     * Set success message to carry forward attributes
     * @param message
     * @param request
     */
    public static void setSuccessMessage(String message, HttpServletRequest request) {
    		getCarryForwardAttributes(request).put(SUCCESS_MESSAGE_ID, message);
    }
	
    /**
     * Set error message to carry forward attributes
     * @param message
     * @param request
     */
    public static void setErrorMessage(String message, HttpServletRequest request) {
		getCarryForwardAttributes(request).put(ERROR_MESSAGE_ID, message);
	}
	
	/**
	 * Add attribute to carry forward attributes
	 * @param key
	 * @param value
	 * @param request
	 */
    public static void addCarryForwardAttribute(String key, Object value, HttpServletRequest request) {
		Map<String, Object> attributes = getCarryForwardAttributes(request);
		attributes.put(key, value);
		request.getSession().setAttribute(CARRY_FORWARD_ATTRIBUTES_ID, attributes);
	} 
	
	/**
	 * Clean up carry forward attributes
	 * @param request
	 */
    public static void cleanupCarryForwardAttributes(HttpServletRequest request) {
		request.getSession().setAttribute(CARRY_FORWARD_ATTRIBUTES_ID, new HashMap<String, Object>());
	}

}
