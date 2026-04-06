package com.example.multiboard.member.controller;

import java.security.Principal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.multiboard.member.MemberValidator;
import com.example.multiboard.member.model.Member;
import com.example.multiboard.member.service.IMemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IMemberService memberService;

    @Autowired
    MemberValidator memberValidator;
    
    @Autowired
    PasswordEncoder	passwordEncoder;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(memberValidator);
    }

    @GetMapping(value="/member/insert")
    public String insertMember(HttpSession session, Model model) {
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);
        logger.info("/member/insert, GET - CSRF Token: {}", csrfToken);
        model.addAttribute("member", new Member());
        return "member/form";
    }
    
    @PostMapping(value="/member/insert")
    public String insertMember(@Validated Member member, BindingResult result,
    		HttpSession session, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("member", member);
            return "member/form";
        }

        try {
            if (!member.getPassword().equals(member.getPassword2())) {
                model.addAttribute("member", member);
                model.addAttribute("message", "MEMBER_PW_RE");
                return "member/form";
            }
            String encodedPw = passwordEncoder.encode(member.getPassword());
        	member.setPassword(encodedPw);
            memberService.insertMember(member);
        } catch (DuplicateKeyException e) {
            member.setUserid(null);
            model.addAttribute("member", member);
            model.addAttribute("message", "ID_ALREADY_EXIST");
            return "member/form";
        }
        
        session.invalidate();
        return "home";
    }

    @GetMapping("/member/login")
    public String login() {
        return "member/login";
    }
//
//    @PostMapping("/member/login")
//    public String login(String userid, String password, HttpSession session, Model model) {
//        Member member = memberService.selectMember(userid);
//        if (member != null) {
//            logger.info(member.toString());
//            String dbPassword = member.getPassword();
//            if (dbPassword.equals(password)) { // 비밀번호 일치
//                session.setMaxInactiveInterval(600); // 세션 타임아웃 10분
//                session.setAttribute("userid", userid);
//                session.setAttribute("name", member.getName());
//                session.setAttribute("email", member.getEmail());
//            } else { // 비밀번호가 다름
//                model.addAttribute("message", "WRONG_PASSWORD");
//            }
//        } else { // 아이디가 없음
//            session.invalidate();
//            model.addAttribute("message", "USER_NOT_FOUND");
//        }
//        return "member/login";
//    }
//
//    @GetMapping("/member/logout")
//    public String logout(HttpSession session, HttpServletRequest request) {
//        session.invalidate();
//        return "home";
//    }

    @GetMapping("/member/update")
    public String updateMember(Model model) {
    	Authentication	auth =	SecurityContextHolder.getContext().getAuthentication();
    	String userid = auth.getName();
        if (userid != null && !userid.equals("")) {
            Member member = memberService.selectMember(userid);
            model.addAttribute("member", member);
            model.addAttribute("message", "UPDATE_USER_INFO");
            return "member/update";
        } else {
            model.addAttribute("message", "NOT_LOGIN_USER");
            return "member/login";
        }
    }

    @PostMapping(value="/member/update")
    public String updateMember(@Validated Member member, BindingResult result, 
                               Principal principal, Model model) {
    	
    	member.setUserid(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("member", member);
            return "member/update";
        }
        
        try {
        	String	encodedPw =	passwordEncoder.encode(member.getPassword());
        	member.setPassword(encodedPw);
        	memberService.updateMember(member);
            model.addAttribute("message", "UPDATED_MEMBER_INFO");
            model.addAttribute("member", member);
//            session.setAttribute("email", member.getEmail());
            return "member/login";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            e.printStackTrace();
            return "member/error";
        }
    }

    @GetMapping("/member/delete")
    public String deleteMember(Principal principal, Model model) {
    	String	userid = principal.getName();
        if (userid != null && !userid.equals("")) {
            Member member = memberService.selectMember(userid);
            model.addAttribute("member", member);
            model.addAttribute("message", "MEMBER_PW_RE");
            return "member/delete";
        } else {
            model.addAttribute("message", "NOT_LOGIN_USER");
            return "member/login";
        }
    }

    @PostMapping("/member/delete")
    public String deleteMember(String password, Principal principal, RedirectAttributes model) {
        try {
            Member member = new Member();
            member.setUserid(principal.getName());
            String dbpw = memberService.getPassword(member.getUserid());
            
            if (password != null && passwordEncoder.matches(password, dbpw)) {
                member.setPassword(dbpw);
                memberService.deleteMember(member);
                model.addFlashAttribute("message", "DELETED_USER_INFO");
                return "redirect:/member/logout";
            } else {
                model.addAttribute("message", "WRONG_PASSWORD");
                return "member/delete";
            }
        } catch (Exception e) {
            model.addAttribute("message", "DELETE_FAIL");
            e.printStackTrace();
            return "member/delete";
        }
    }
}