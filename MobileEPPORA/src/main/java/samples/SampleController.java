package samples;

import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Controller
public class SampleController {

	@RequestMapping(value="/greetme", method=RequestMethod.GET)
	public ModelAndView GreetingControll(){
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
		
		jdbcTestTemplate test = (jdbcTestTemplate)context.getBean("jdbcTestTemplate");
		List<jdbcTest> testers = test.listRows();
		for(jdbcTest record : testers){
			System.out.print("ID :"+ record.getId());
			System.out.print(", Name :"+ record.getName());
			System.out.print(", Short Info :" + record.getShortInfo());
			System.out.println(", Description :" + record.getDescription());
		}
		ModelAndView mav = new ModelAndView("greetme");
		
		mav.addObject("ps", testers.get(0));
		return mav;
	}
	
/*
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap model) {
        return "login";
    }
 
    @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
    public String loginerror(ModelMap model) {
        model.addAttribute("error", "true");
        return "denied";
    }
 
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(ModelMap model) {
        return "logout";
    }
    */
}
