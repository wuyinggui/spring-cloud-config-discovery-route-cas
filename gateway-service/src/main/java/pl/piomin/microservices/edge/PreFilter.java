package pl.piomin.microservices.edge;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.http.HttpStatus;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class PreFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object run() {
			RequestContext ctx = RequestContext.getCurrentContext();
			HttpServletRequest request = ctx.getRequest();
			Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
			if(assertion != null){
				AttributePrincipal principal = assertion.getPrincipal();
				if(principal == null){
					ctx.setResponseBody("invalid token");
					ctx.setSendZuulResponse(false);
					ctx.setResponseStatusCode(HttpStatus.OK.value());
					throw new RuntimeException(); 
				}
			}else{
				ctx.setResponseBody("invalid token");
				ctx.setSendZuulResponse(false);
				ctx.setResponseStatusCode(HttpStatus.OK.value());
				throw new RuntimeException(); 
			}
			return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "pre";
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 6;
	}

}
