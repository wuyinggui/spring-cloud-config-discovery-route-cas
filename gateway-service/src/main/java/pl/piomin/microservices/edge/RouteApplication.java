package pl.piomin.microservices.edge;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.ModelAndView;

import net.unicon.cas.client.configuration.EnableCasClient;


@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@RestController
@EnableCasClient
public class RouteApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(RouteApplication.class).web(true).run(args);
	}
	@RequestMapping("/cas/welcome")
	public String cas(HttpServletRequest request){
		Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
		AttributePrincipal principal = assertion.getPrincipal();
		if(principal == null){
			return "token valid";
		}
		String userName = principal.getName();
		return "hello "+ userName;
	}
	@RequestMapping("/logout")
	public ModelAndView logout(HttpServletRequest request, ModelAndView mav) {
        HttpSession session=request.getSession();
        session.invalidate();
        String viewName="redirect:http://10.168.0.187:8080/logout?service=http://localhost:8765/cas/welcome";
    	mav.setViewName(viewName);
		return mav;
    } 
	@RequestMapping("/hello")
	public String hello(HttpServletRequest request){
		return "hello world";
	}
	@Bean
	public PreFilter preFilter(){
		return new PreFilter();
	}
	@Bean
	public CorsFilter corsFilter() {
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    final CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    config.addAllowedOrigin("*");
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("OPTIONS");
	    config.addAllowedMethod("HEAD");
	    config.addAllowedMethod("GET");
	    config.addAllowedMethod("PUT");
	    config.addAllowedMethod("POST");
	    config.addAllowedMethod("DELETE");
	    config.addAllowedMethod("PATCH");
	    config.addAllowedMethod("cas");
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}
}
