package com.example.demo;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping(path = "/home")
public class home {
	private RestTemplate rest = new RestTemplate();
	
	@GetMapping()
	public String home() {
		//Match[] match =rest.getForObject("http://localhost:8092/matches/"+matchID,Match[].class);
		return "home";
	}


	@PostMapping()
	public String editdone( HttpServletRequest request,Model model) {
		
		
		String name=(String) request.getParameter("name");
		String matchID=(String) request.getParameter("matchId");
		String forqrcode="";
		int number=Integer.parseInt( request.getParameter("number"));
		Customer customer=new Customer();
		customer.setName(name);
		Customer cus= rest.postForObject("http://localhost:8091/customers/add/", customer, Customer.class);
		Ticket[] list=rest.getForObject("http://localhost:8092/matches/get/available/"+matchID,Ticket[].class);
		for(int i=0;i<number;i++) {
			Booking booking=new Booking(matchID,list[i].getId(),cus.getId());
			rest.postForObject("http://localhost:8093/bookings/add", booking,String.class);
			forqrcode+=booking.toString();
			
		}
		String urlqrcode="https://qrickit.com/api/qr.php?d="+forqrcode+"&addtext=Hello+World&txtcolor=442EFF&fgdcolor=76103C\r\n"
				+ "&bgdcolor=C0F912&qrsize=150&t=p&e=m" ;
		String url="https://qrickit.com/api/qr.php?d="+forqrcode;
		model.addAttribute("qr",url);
		
		return "ticket";
	}



}
