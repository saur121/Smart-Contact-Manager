package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.entities.*;
import com.smart.helper.Message;
import com.smart.dao.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/test")
	@ResponseBody
	public String test()
	{
		User u = new User();
		u.setName("Krishna Panchal");
		u.setEmail("krish@gmail.com");
		Contact c=new Contact();
		u.getContact().add(c);
		userRepository.save(u);
		return "Working";
	}
	
	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title","Welcome page");
		return "home";
	}
	
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title","Sign Up");
		m.addAttribute("user",new User());
		return "signup";
	}
	
	//handler for register user
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement",defaultValue="false") boolean agreement,Model model,HttpSession session)
	{
		try {
			
			if(result.hasErrors())
			{
				System.out.println(result);
				model.addAttribute("user", user);
				return "signup";
			}
			if(!agreement)
			{
				System.out.println("false");
				throw new Exception("You have not agree terms and conditions");
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			this.userRepository.save(user);
			
			System.out.println("Agg "+agreement);
			System.out.println("User "+user);
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully Register!!","alert-success"));
			return "signup";
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something Went Wrong!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
		
	}
	
	@RequestMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","login Page");
		return "login";
	}
}
