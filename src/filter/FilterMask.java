package filter;

import java.io.IOException;
import java.util.Collection;
import java.util.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import bean.BookStats;
/**
 * Servlet Filter implementation class FilterMask
 */
@WebFilter("/FilterMask")
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
		// TODO Auto-generated method stub
		// place your code here
		
		
		HttpServletRequest req = (HttpServletRequest) request;
		@SuppressWarnings("unchecked")
		Collection<BookStats> bookStats = (Collection<BookStats>) req.getSession().getAttribute("stats");
		List <BookStats> masking = null;
		
		if (bookStats != null)
		{
			masking = new ArrayList<BookStats>(bookStats);
		}

		if (bookStats != null)
		{
			for (int i = 0; i < masking.size(); i++)
			{ 		
				String temp = masking.get(i).getEmail();
				masking.get(i).setEmail(temp.substring(0, 2) + "****");
			}
			req.getSession().setAttribute("stats", masking);
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
