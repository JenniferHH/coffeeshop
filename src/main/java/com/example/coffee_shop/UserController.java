package com.example.coffee_shop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;


    public UserController(UserRepository userRepository, JWTUtil jwtUtil, UserService userService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/product")
    public String product(HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        return "product";
    }

    @GetMapping("/arabica")
    public String arabica(HttpSession session, Model model){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        return "arabica";
    }

    @GetMapping("/bourbon")
    public String bourbon(HttpSession session, Model model){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        return "bourbon";
    }

    @GetMapping("/excelsa")
    public String excelsa(HttpSession session, Model model){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        return "excelsa";
    }

    @GetMapping("/liberica")
    public String liberica(HttpSession session, Model model){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        return "liberica";
    }

    @GetMapping("/robusta")
    public String robusta(HttpSession session, Model model){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        return "robusta";
    }

    @GetMapping("/typica")
    public String typica(HttpSession session, Model model){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        model.addAttribute("cartItems", cartItems);
        return "typica";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model){
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        double totalAmount = 0.0;
        for (CartItem item : cartItems) {
            totalAmount += item.getQuality() * item.getPrice();
        }

        DecimalFormat df = new DecimalFormat("#.00");
        String formattedTotalAmount = df.format(totalAmount);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", formattedTotalAmount);

        return "cart";
    }

    @PostMapping ("/add")
    public String add(@RequestParam String name, @RequestParam int quality, @RequestParam double price, @NotNull HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        CartItem newItem = new CartItem(name, quality, price);
        cartItems.add(newItem);

        model.addAttribute("cartItems", cartItems);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam("index") int index, HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null && index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
            session.setAttribute("cartItems", cartItems);
        }
        return "redirect:/cart";
    }

    @PostMapping("/removeitem")
    public String removeitem(@RequestParam("index") int index, HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null && index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
            session.setAttribute("cartItems", cartItems);
        }
        return "redirect:/user-detail";
    }

    @GetMapping("/login")
    public String login(HttpSession httpSession) {
        String token = (String) httpSession.getAttribute("token");
        if (token != null && jwtUtil.validateToken(token)) {
            User existingUser = userRepository.findByEmail(jwtUtil.extractEmail(token));
            if (existingUser.getType().equals("user")) {
                return "redirect:/user-detail";
            } else if (existingUser.getType().equals("admin")) {
                return "redirect:/admin-detial";
            }
            return "redirect:/login";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(User user, RedirectAttributes redirectAttributes, HttpSession session) {
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null && user.getPassword().equals(existingUser.getPassword())) {
            String token = jwtUtil.generateToken(existingUser.getEmail());
            redirectAttributes.addAttribute("token", token);

            if (existingUser.getType().equals("user")) {
                return "redirect:/user-detail";
            } else if (existingUser.getType().equals("admin")) {
                return "redirect:/admin-detail";
            }
        }

        return "redirect:/login?error";
    }


    @GetMapping("/signup")
    public String addUserForm(Model model) {
        User user = new User();
        user.setType("user");
        model.addAttribute("user", user);
        return "signup";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    @GetMapping("/user-detail")
    public String userDetail(Model model, HttpServletRequest request, HttpSession session) {
        String token = request.getParameter("token");
        if (token != null && jwtUtil.validateToken(token)) {
            Order order = new Order();
            model.addAttribute("order", order);

            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }

            double totalAmount = 0.0;
            for (CartItem item : cartItems) {
                totalAmount += item.getQuality() * item.getPrice();
            }

            DecimalFormat df = new DecimalFormat("#.00");
            String formattedTotalAmount = df.format(totalAmount);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", formattedTotalAmount);


            return "user-detail";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/saveOrder")
    public String saveOrder(@ModelAttribute Order order, HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        double totalAmount = 0.0;
        for (CartItem item : cartItems) {
            totalAmount += item.getQuality() * item.getPrice();
        }

        order.setCost(totalAmount);

        model.addAttribute("cartItems", cartItems);
        orderService.saveOrder(order);

        return "redirect:/";
    }


    @GetMapping("/admin-detail")
    public String adminDetail(Model model, HttpServletRequest request) {
        String token = request.getParameter("token");

        if (token != null && jwtUtil.validateToken(token)) {
            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("orders", orders);
            return "admin-detail";
        } else {
            return "redirect:/login";
        }
    }
}
