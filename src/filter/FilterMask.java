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

/**
 * Servlet Filter implementation class FilterMask
 */
@WebFilter(
		dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD }
					, 
		urlPatterns = { 
				"/Start/Analytics"
		})
public class FilterMask implements Filter {

    /**
     * Default constructor. 
     */
    public FilterMask() {
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
		HttpServletRequest req = (HttpServletRequest) request;
		
		String path = req.getRequestURI().substring(req.getContextPath().length());
		
		String submit = req.getParameter("submit");
		// debug
		System.out.println(String.format("Filter: Path=%s  submit=%s", path,submit));
		
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
