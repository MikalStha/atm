package com.puffinpowered.atm.controller;

import com.puffinpowered.atm.domain.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class Welcome {

	private static final Logger log = LoggerFactory.getLogger(Welcome.class);

	@Value("${homepage.image}")
	private String image;

	@RequestMapping(method = RequestMethod.GET)
	public String welcome(Model model){
		model.addAttribute("image",image);
		model.addAttribute("initiliased", false);
		model.addAttribute("money", new Money());
		model.addAttribute("message","");
		return "home/welcome";
	}

	@ExceptionHandler(Exception.class)
	public String handleException(HttpServletRequest req, Exception exception, Model model){
		model.addAttribute("message", exception.getMessage() );
		model.addAttribute("error", "Not Found" );
		return "error/error";
	}
}
