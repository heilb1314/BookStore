package filter;

import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import model.SessionAttributeManager;

/**
 * Servlet Filter implementation class CarryForwardAttributesFilter
 */
@WebFilter(dispatcherTypes = {DispatcherType.REQUEST }
					, urlPatterns = { "/*" })
public class CarryForwardAttributesFilter implements Filter {

    /**
     * Default constructor. 
     */
    public CarryForwardAttributesFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		// transfer possible carry forward attributes to request scope
		HttpServletRequest r = (HttpServletRequest) request;
        String path = r.getRequestURI().substring(r.getContextPath().length());
        
        if (r.getMethod().equalsIgnoreCase("POST")) {
        		System.out.println("Carry forward filter [POST]: " + path);
        		SessionAttributeManager.cleanupCarryForwardAttributes(r);
        } else {
        		System.out.println("Carry forward filter [GET]: " + path);
        		SessionAttributeManager.transferAttributesToRequestScope(r);
        		SessionAttributeManager.cleanupCarryForwardAttributes(r);
        }

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
